package com.ute.library.controller;

import com.ute.library.model.Reader;
import com.ute.library.model.ReaderForm;
import com.ute.library.repository.BorrowSlipRepository;
import com.ute.library.repository.ReaderRepository;
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
public class ReaderController {

    private static final String READER_MANAGE_VIEW = "members-manage";

    private final ReaderRepository readerRepository;
    private final BorrowSlipRepository borrowSlipRepository;

    @GetMapping("/members-manage")
    public String membersManage(@RequestParam(name = "editId", required = false) Integer editId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        ReaderForm readerForm = new ReaderForm();

        if (editId != null) {
            Optional<Reader> readerOptional = readerRepository.findById(editId);
            if (readerOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy độc giả cần chỉnh sửa.");
                return "redirect:/members-manage";
            }
            readerForm = toForm(readerOptional.get());
        }

        populateModel(model, readerForm);
        return READER_MANAGE_VIEW;
    }

    @GetMapping("/members")
    public String membersPage() {
        return "redirect:/members-manage";
    }

    @PostMapping("/readers")
    public String saveReader(@Valid @ModelAttribute("readerForm") ReaderForm readerForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        Reader reader;
        boolean isNewReader;

        if (readerForm.getId() == null) {
            reader = new Reader();
            isNewReader = true;
        } else {
            Optional<Reader> readerOptional = readerRepository.findById(readerForm.getId());
            if (readerOptional.isEmpty()) {
                bindingResult.rejectValue("id", "id.invalid", "Không tìm thấy độc giả cần cập nhật");
                populateModel(model, readerForm);
                return READER_MANAGE_VIEW;
            }
            reader = readerOptional.get();
            isNewReader = false;
        }

        if (bindingResult.hasErrors()) {
            populateModel(model, readerForm);
            return READER_MANAGE_VIEW;
        }

        Optional<Reader> existingReaderByCode = readerRepository.findByReaderCode(readerForm.getReaderCode());
        if (existingReaderByCode.isPresent() && (readerForm.getId() == null || !existingReaderByCode.get().getId().equals(readerForm.getId()))) {
            bindingResult.rejectValue("readerCode", "readerCode.duplicate", "Mã độc giả đã tồn tại");
            populateModel(model, readerForm);
            return READER_MANAGE_VIEW;
        }

        Optional<Reader> existingReaderByEmail = readerRepository.findByEmail(readerForm.getEmail());
        if (existingReaderByEmail.isPresent() && (readerForm.getId() == null || !existingReaderByEmail.get().getId().equals(readerForm.getId()))) {
            bindingResult.rejectValue("email", "email.duplicate", "Email đã được đăng ký");
            populateModel(model, readerForm);
            return READER_MANAGE_VIEW;
        }

        Optional<Reader> existingReaderByPhone = readerRepository.findByPhone(readerForm.getPhone());
        if (existingReaderByPhone.isPresent() && (readerForm.getId() == null || !existingReaderByPhone.get().getId().equals(readerForm.getId()))) {
            bindingResult.rejectValue("phone", "phone.duplicate", "Số điện thoại đã được đăng ký");
            populateModel(model, readerForm);
            return READER_MANAGE_VIEW;
        }

        applyFormToReader(reader, readerForm, isNewReader);
        readerRepository.save(reader);

        redirectAttributes.addFlashAttribute("successMessage", isNewReader ? "Đã thêm độc giả mới thành công." : "Đã cập nhật thông tin độc giả.");
        return "redirect:/members-manage";
    }

    @PostMapping("/readers/{id}/delete")
    public String deleteReader(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (!readerRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy độc giả để xóa.");
            return "redirect:/members-manage";
        }

        if (borrowSlipRepository.existsByReader_Id(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa độc giả đang có phiếu mượn.");
            return "redirect:/members-manage";
        }

        readerRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa độc giả khỏi danh sách.");
        return "redirect:/members-manage";
    }

    private void populateModel(Model model, ReaderForm readerForm) {
        model.addAttribute("readers", readerRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("readerForm", readerForm);
    }

    private ReaderForm toForm(Reader reader) {
        ReaderForm form = new ReaderForm();
        form.setId(reader.getId());
        form.setReaderCode(reader.getReaderCode());
        form.setFullName(reader.getFullName());
        form.setEmail(reader.getEmail());
        form.setPhone(reader.getPhone());
        form.setAddress(reader.getAddress());
        form.setStatus(reader.getStatus());
        return form;
    }

    private void applyFormToReader(Reader reader, ReaderForm form, boolean isNewReader) {
        reader.setReaderCode(form.getReaderCode().trim());
        reader.setFullName(form.getFullName().trim());
        reader.setEmail(form.getEmail().trim());
        reader.setPhone(form.getPhone().trim());
        reader.setAddress(form.getAddress() != null ? form.getAddress().trim() : null);
        reader.setStatus(form.getStatus().trim());

        LocalDateTime now = LocalDateTime.now();
        if (isNewReader || reader.getCreatedAt() == null) {
            reader.setCreatedAt(now);
        }
        reader.setUpdatedAt(now);
    }
}

