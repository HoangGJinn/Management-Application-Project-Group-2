package com.ute.library.controller;

import com.ute.library.model.Book;
import com.ute.library.model.BookForm;
import com.ute.library.model.Category;
import com.ute.library.model.Publisher;
import com.ute.library.repository.BookRepository;
import com.ute.library.repository.BorrowItemRepository;
import com.ute.library.repository.CategoryRepository;
import com.ute.library.repository.PublisherRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BookController {

    private static final String BOOK_MANAGE_VIEW = "books-manage";
    private static final String DEFAULT_STATUS = "AVAILABLE";

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final BorrowItemRepository borrowItemRepository;

    @GetMapping("/books-manage")
    public String booksManage(@RequestParam(name = "editId", required = false) Integer editId,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        BookForm bookForm = new BookForm();

        if (editId != null) {
            Optional<Book> bookOptional = bookRepository.findById(editId);
            if (bookOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sách cần chỉnh sửa.");
                return "redirect:/books-manage";
            }
            bookForm = toForm(bookOptional.get());
        }

        populateModel(model, bookForm);
        return BOOK_MANAGE_VIEW;
    }

    @GetMapping("/books")
    public String booksPage() {
        return "redirect:/books-manage";
    }

    @PostMapping("/books")
    public String saveBook(@Valid @ModelAttribute("bookForm") BookForm bookForm,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        validateBusinessRules(bookForm, bindingResult);

        Book book;
        boolean isNewBook;

        if (bookForm.getId() == null) {
            book = new Book();
            isNewBook = true;
        } else {
            Optional<Book> bookOptional = bookRepository.findById(bookForm.getId());
            if (bookOptional.isEmpty()) {
                bindingResult.rejectValue("id", "id.invalid", "Không tìm thấy sách cần cập nhật");
                populateModel(model, bookForm);
                return BOOK_MANAGE_VIEW;
            }
            book = bookOptional.get();
            isNewBook = false;
        }

        if (bindingResult.hasErrors()) {
            populateModel(model, bookForm);
            return BOOK_MANAGE_VIEW;
        }

        Category category = categoryRepository.findById(bookForm.getCategoryId()).orElse(null);
        Publisher publisher = publisherRepository.findById(bookForm.getPublisherId()).orElse(null);

        if (category == null) {
            bindingResult.rejectValue("categoryId", "categoryId.invalid", "Thể loại không hợp lệ");
        }
        if (publisher == null) {
            bindingResult.rejectValue("publisherId", "publisherId.invalid", "Nhà xuất bản không hợp lệ");
        }

        if (bindingResult.hasErrors()) {
            populateModel(model, bookForm);
            return BOOK_MANAGE_VIEW;
        }

        Optional<Book> existingBookByCode = bookRepository.findByBookCode(bookForm.getBookCode());
        if (existingBookByCode.isPresent() && (bookForm.getId() == null || !existingBookByCode.get().getId().equals(bookForm.getId()))) {
            bindingResult.rejectValue("bookCode", "bookCode.duplicate", "Mã sách đã tồn tại");
            populateModel(model, bookForm);
            return BOOK_MANAGE_VIEW;
        }

        applyFormToBook(book, bookForm, category, publisher, isNewBook);
        bookRepository.save(book);

        redirectAttributes.addFlashAttribute("successMessage", isNewBook ? "Đã thêm sách mới thành công." : "Đã cập nhật thông tin sách.");
        return "redirect:/books-manage";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (!bookRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sách để xóa.");
            return "redirect:/books-manage";
        }

        if (borrowItemRepository.existsByBook_Id(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa sách đang được sử dụng trong phiếu mượn.");
            return "redirect:/books-manage";
        }

        bookRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sách khỏi danh sách.");
        return "redirect:/books-manage";
    }

    private void populateModel(Model model, BookForm bookForm) {
        model.addAttribute("books", bookRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "categoryName")));
        model.addAttribute("publishers", publisherRepository.findAll(Sort.by(Sort.Direction.ASC, "publisherName")));
        model.addAttribute("bookForm", bookForm);
    }

    private BookForm toForm(Book book) {
        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setBookCode(book.getBookCode());
        form.setTitle(book.getTitle());
        form.setAuthor(book.getAuthor());
        form.setCategoryId(book.getCategory() != null ? book.getCategory().getId() : null);
        form.setPublisherId(book.getPublisher() != null ? book.getPublisher().getId() : null);
        form.setPublishYear(book.getPublishYear());
        form.setQuantity(book.getQuantity());
        form.setAvailableQuantity(book.getAvailableQuantity());
        form.setStatus(book.getStatus());
        return form;
    }

    private void applyFormToBook(Book book, BookForm form, Category category, Publisher publisher, boolean isNewBook) {
        book.setBookCode(form.getBookCode().trim());
        book.setTitle(form.getTitle().trim());
        book.setAuthor(form.getAuthor().trim());
        book.setCategory(category);
        book.setPublisher(publisher);
        book.setPublishYear(form.getPublishYear());
        book.setQuantity(form.getQuantity());
        book.setAvailableQuantity(form.getAvailableQuantity());
        book.setStatus(form.getStatus().trim());

        LocalDateTime now = LocalDateTime.now();
        if (isNewBook || book.getCreatedAt() == null) {
            book.setCreatedAt(now);
        }
        book.setUpdatedAt(now);
    }

    private void validateBusinessRules(BookForm bookForm, BindingResult bindingResult) {
        if (bookForm.getQuantity() != null && bookForm.getAvailableQuantity() != null
                && bookForm.getAvailableQuantity() > bookForm.getQuantity()) {
            bindingResult.rejectValue("availableQuantity", "availableQuantity.invalid",
                    "Số lượng còn lại không được lớn hơn số lượng tổng");
        }
    }
}


