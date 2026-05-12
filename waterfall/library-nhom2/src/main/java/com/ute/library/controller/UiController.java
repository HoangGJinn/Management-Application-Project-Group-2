package com.ute.library.controller;

import com.ute.library.model.Book;
import com.ute.library.model.BorrowItem;
import com.ute.library.model.BorrowSlip;
import com.ute.library.model.Reader;
import com.ute.library.repository.BookRepository;
import com.ute.library.repository.BorrowItemRepository;
import com.ute.library.repository.BorrowSlipRepository;
import com.ute.library.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UiController {

    private static final String BOOK_ACTIVE = "ACTIVE";
    private static final String SLIP_BORROWING = "BORROWING";
    private static final String SLIP_RETURNED = "RETURNED";
    private static final String ITEM_BORROWING = "BORROWING";
    private static final String ITEM_RETURNED = "RETURNED";

    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final BorrowSlipRepository borrowSlipRepository;
    private final BorrowItemRepository borrowItemRepository;

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping({"/index", "/home"})
    public String index() {
        return "index";
    }



    @GetMapping("/loans-manage")
    public String loansManage() {
        return "loans-manage";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }

    // -- API endpoints backed by SQL/JPA --
    @GetMapping("/api/books")
    @ResponseBody
    public List<Map<String, Object>> apiBooks() {
        return bookRepository.findAll().stream()
                .map(this::toBookPayload)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/members")
    @ResponseBody
    public List<Map<String, Object>> apiMembers() {
        return readerRepository.findAll().stream()
                .map(this::toReaderPayload)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/loans")
    @ResponseBody
    public List<Map<String, Object>> apiLoans() {
        List<BorrowItem> activeItems = borrowItemRepository
                .findByItemStatusAndBorrowSlip_StatusOrderByBorrowSlip_DueDateAscIdDesc(ITEM_BORROWING, SLIP_BORROWING);
        return activeItems.stream().map(this::toLoanPayload).collect(Collectors.toList());
    }

    @GetMapping("/api/loans/overdue")
    @ResponseBody
    public List<Map<String, Object>> apiOverdueLoans() {
        List<BorrowItem> overdueItems = borrowItemRepository
                .findByItemStatusAndBorrowSlip_StatusAndBorrowSlip_DueDateBeforeOrderByBorrowSlip_DueDateAscIdDesc(
                        ITEM_BORROWING,
                        SLIP_BORROWING,
                        LocalDate.now()
                );
        return overdueItems.stream().map(this::toLoanPayload).collect(Collectors.toList());
    }

    @GetMapping("/api/dashboard/stats")
    @ResponseBody
    public Map<String, Object> dashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalMembers = readerRepository.count();
        long borrowedBooks = borrowItemRepository
                .findByItemStatusAndBorrowSlip_StatusOrderByBorrowSlip_DueDateAscIdDesc(ITEM_BORROWING, SLIP_BORROWING)
                .stream()
                .mapToLong(item -> item.getQuantity() == null ? 1L : item.getQuantity())
                .sum();
        long overdueBooks = borrowItemRepository
                .findByItemStatusAndBorrowSlip_StatusAndBorrowSlip_DueDateBeforeOrderByBorrowSlip_DueDateAscIdDesc(
                        ITEM_BORROWING,
                        SLIP_BORROWING,
                        LocalDate.now()
                )
                .stream()
                .mapToLong(item -> item.getQuantity() == null ? 1L : item.getQuantity())
                .sum();

        stats.put("totalMembers", totalMembers);
        stats.put("borrowedBooks", borrowedBooks);
        stats.put("overdueBooks", overdueBooks);
        stats.put("newMembers", totalMembers);
        return stats;
    }

    @PostMapping("/api/loans/{id}/return")
    @ResponseBody
    @Transactional
    public Map<String, Object> returnLoan(@PathVariable("id") Integer id,
                                          @RequestBody(required = false) Map<String, Object> payload) {
        BorrowItem item = borrowItemRepository.findById(id).orElse(null);
        if (item == null || !ITEM_BORROWING.equalsIgnoreCase(item.getItemStatus())) {
            return Map.of("success", false, "message", "Phiếu mượn không hợp lệ hoặc đã được trả.");
        }

        LocalDate returnDate = LocalDate.now();
        if (payload != null && payload.get("returnDate") != null) {
            returnDate = LocalDate.parse(String.valueOf(payload.get("returnDate")));
        }
        String note = payload != null && payload.get("notes") != null ? String.valueOf(payload.get("notes")) : null;

        item.setItemStatus(ITEM_RETURNED);
        item.setReturnDate(returnDate);
        item.setUpdatedAt(LocalDateTime.now());
        borrowItemRepository.save(item);

        BorrowSlip slip = item.getBorrowSlip();
        if (slip != null) {
            if (note != null && !note.isBlank()) {
                slip.setNote(note.trim());
            }
            boolean hasOpenItems = borrowItemRepository.existsByBorrowSlip_IdAndItemStatus(slip.getId(), ITEM_BORROWING);
            if (!hasOpenItems) {
                slip.setStatus(SLIP_RETURNED);
                slip.setClosedAt(LocalDateTime.now());
            }
            slip.setUpdatedAt(LocalDateTime.now());
            borrowSlipRepository.save(slip);
        }

        Book book = item.getBook();
        if (book != null) {
            int currentAvailable = book.getAvailableQuantity() == null ? 0 : book.getAvailableQuantity();
            int quantityReturned = item.getQuantity() == null ? 1 : item.getQuantity();
            int totalQuantity = book.getQuantity() == null ? currentAvailable + quantityReturned : book.getQuantity();
            book.setAvailableQuantity(Math.min(totalQuantity, currentAvailable + quantityReturned));
            if (book.getStatus() == null || book.getStatus().isBlank()) {
                book.setStatus(BOOK_ACTIVE);
            }
            book.setUpdatedAt(LocalDateTime.now());
            bookRepository.save(book);
        }

        return Map.of("success", true, "message", "Đã ghi nhận trả sách thành công.");
    }

    // Placeholders for modifying data — currently return 501-like messages via simple strings
    @PostMapping("/api/books/{id}/borrow")
    @ResponseBody
    public Map<String, Object> borrowBook(@PathVariable("id") Long id) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "TODO");
        resp.put("message", "Implement borrowBook(id) to update database or call service");
        resp.put("bookId", id);
        return resp;
    }

    @PostMapping("/api/books")
    @ResponseBody
    public Map<String, Object> createBook(@RequestBody Map<String, Object> book) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "TODO");
        resp.put("message", "Implement createBook(book) to persist a new book");
        resp.put("received", book);
        return resp;
    }

    private Map<String, Object> toBookPayload(Book book) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", book.getId());
        payload.put("title", book.getTitle());
        payload.put("author", book.getAuthor());
        payload.put("status", book.getStatus());
        return payload;
    }

    private Map<String, Object> toReaderPayload(Reader reader) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", reader.getId());
        payload.put("name", reader.getFullName());
        payload.put("email", reader.getEmail());
        payload.put("phone", reader.getPhone());
        return payload;
    }

    private Map<String, Object> toLoanPayload(BorrowItem item) {
        BorrowSlip slip = item.getBorrowSlip();
        Reader reader = slip != null ? slip.getReader() : null;
        Book book = item.getBook();
        LocalDate dueDate = slip != null ? slip.getDueDate() : null;
        LocalDate borrowDate = slip != null ? slip.getBorrowDate() : null;

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", item.getId());
        payload.put("slipId", slip != null ? slip.getId() : null);
        payload.put("bookId", book != null ? book.getId() : null);
        payload.put("bookTitle", book != null ? book.getTitle() : null);
        payload.put("memberId", reader != null ? reader.getId() : null);
        payload.put("memberName", reader != null ? reader.getFullName() : null);
        payload.put("borrowDate", borrowDate != null ? borrowDate.toString() : null);
        payload.put("dueDate", dueDate != null ? dueDate.toString() : null);
        payload.put("status", item.getItemStatus());
        payload.put("quantity", item.getQuantity());
        return payload;
    }

}
