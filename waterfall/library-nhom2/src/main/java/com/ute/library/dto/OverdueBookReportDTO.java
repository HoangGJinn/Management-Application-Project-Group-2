package com.ute.library.dto;

import java.time.LocalDate;

public class OverdueBookReportDTO {
    private String slipCode;
    private String readerCode;
    private String readerName;
    private String email;
    private String phone;
    private String bookCode;
    private String bookTitle;
    private int quantity;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private int overdueDays;

    public OverdueBookReportDTO() {
    }

    public OverdueBookReportDTO(String slipCode, String readerCode, String readerName, String email,
                                String phone, String bookCode, String bookTitle, int quantity,
                                LocalDate borrowDate, LocalDate dueDate, int overdueDays) {
        this.slipCode = slipCode;
        this.readerCode = readerCode;
        this.readerName = readerName;
        this.email = email;
        this.phone = phone;
        this.bookCode = bookCode;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.overdueDays = overdueDays;
    }

    public String getSlipCode() {
        return slipCode;
    }

    public void setSlipCode(String slipCode) {
        this.slipCode = slipCode;
    }

    public String getReaderCode() {
        return readerCode;
    }

    public void setReaderCode(String readerCode) {
        this.readerCode = readerCode;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(int overdueDays) {
        this.overdueDays = overdueDays;
    }
}
