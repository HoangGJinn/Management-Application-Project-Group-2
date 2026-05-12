package com.ute.library.dto;

import java.time.LocalDate;

public class BorrowedBookReportDTO {
    private String slipCode;
    private String readerCode;
    private String readerName;
    private String bookCode;
    private String bookTitle;
    private String author;
    private int quantity;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private String librarianName;
    private String status;

    public BorrowedBookReportDTO() {
    }

    public BorrowedBookReportDTO(String slipCode, String readerCode, String readerName, String bookCode,
                                 String bookTitle, String author, int quantity, LocalDate borrowDate,
                                 LocalDate dueDate, String librarianName, String status) {
        this.slipCode = slipCode;
        this.readerCode = readerCode;
        this.readerName = readerName;
        this.bookCode = bookCode;
        this.bookTitle = bookTitle;
        this.author = author;
        this.quantity = quantity;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.librarianName = librarianName;
        this.status = status;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getLibrarianName() {
        return librarianName;
    }

    public void setLibrarianName(String librarianName) {
        this.librarianName = librarianName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
