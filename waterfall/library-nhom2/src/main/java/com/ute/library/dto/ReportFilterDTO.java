package com.ute.library.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ReportFilterDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    private String readerKeyword;
    private String bookKeyword;
    private Integer categoryId;
    private Integer limit;

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public String getReaderKeyword() {
        return readerKeyword;
    }

    public void setReaderKeyword(String readerKeyword) {
        this.readerKeyword = readerKeyword;
    }

    public String getBookKeyword() {
        return bookKeyword;
    }

    public void setBookKeyword(String bookKeyword) {
        this.bookKeyword = bookKeyword;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
