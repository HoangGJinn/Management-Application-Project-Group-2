package com.ute.library.dto;

public class TopBorrowedBookDTO {
    private String bookCode;
    private String bookTitle;
    private String author;
    private String categoryName;
    private String publisherName;
    private long totalBorrowTimes;
    private long totalBorrowedQuantity;

    public TopBorrowedBookDTO() {
    }

    public TopBorrowedBookDTO(String bookCode, String bookTitle, String author, String categoryName,
                              String publisherName, long totalBorrowTimes, long totalBorrowedQuantity) {
        this.bookCode = bookCode;
        this.bookTitle = bookTitle;
        this.author = author;
        this.categoryName = categoryName;
        this.publisherName = publisherName;
        this.totalBorrowTimes = totalBorrowTimes;
        this.totalBorrowedQuantity = totalBorrowedQuantity;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public long getTotalBorrowTimes() {
        return totalBorrowTimes;
    }

    public void setTotalBorrowTimes(long totalBorrowTimes) {
        this.totalBorrowTimes = totalBorrowTimes;
    }

    public long getTotalBorrowedQuantity() {
        return totalBorrowedQuantity;
    }

    public void setTotalBorrowedQuantity(long totalBorrowedQuantity) {
        this.totalBorrowedQuantity = totalBorrowedQuantity;
    }
}
