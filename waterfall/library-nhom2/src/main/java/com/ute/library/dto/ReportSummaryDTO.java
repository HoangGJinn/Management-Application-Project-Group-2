package com.ute.library.dto;

public class ReportSummaryDTO {
    private long activeBookTitles;
    private long totalBookCopies;
    private long availableBookCopies;
    private long borrowedBookCopies;
    private long activeReaders;
    private long openBorrowSlips;
    private long overdueBookCopies;
    private long lostBookCopies;

    public ReportSummaryDTO() {
    }

    public ReportSummaryDTO(long activeBookTitles, long totalBookCopies, long availableBookCopies,
                            long borrowedBookCopies, long activeReaders, long openBorrowSlips,
                            long overdueBookCopies, long lostBookCopies) {
        this.activeBookTitles = activeBookTitles;
        this.totalBookCopies = totalBookCopies;
        this.availableBookCopies = availableBookCopies;
        this.borrowedBookCopies = borrowedBookCopies;
        this.activeReaders = activeReaders;
        this.openBorrowSlips = openBorrowSlips;
        this.overdueBookCopies = overdueBookCopies;
        this.lostBookCopies = lostBookCopies;
    }

    public long getActiveBookTitles() {
        return activeBookTitles;
    }

    public void setActiveBookTitles(long activeBookTitles) {
        this.activeBookTitles = activeBookTitles;
    }

    public long getTotalBookCopies() {
        return totalBookCopies;
    }

    public void setTotalBookCopies(long totalBookCopies) {
        this.totalBookCopies = totalBookCopies;
    }

    public long getAvailableBookCopies() {
        return availableBookCopies;
    }

    public void setAvailableBookCopies(long availableBookCopies) {
        this.availableBookCopies = availableBookCopies;
    }

    public long getBorrowedBookCopies() {
        return borrowedBookCopies;
    }

    public void setBorrowedBookCopies(long borrowedBookCopies) {
        this.borrowedBookCopies = borrowedBookCopies;
    }

    public long getActiveReaders() {
        return activeReaders;
    }

    public void setActiveReaders(long activeReaders) {
        this.activeReaders = activeReaders;
    }

    public long getOpenBorrowSlips() {
        return openBorrowSlips;
    }

    public void setOpenBorrowSlips(long openBorrowSlips) {
        this.openBorrowSlips = openBorrowSlips;
    }

    public long getOverdueBookCopies() {
        return overdueBookCopies;
    }

    public void setOverdueBookCopies(long overdueBookCopies) {
        this.overdueBookCopies = overdueBookCopies;
    }

    public long getLostBookCopies() {
        return lostBookCopies;
    }

    public void setLostBookCopies(long lostBookCopies) {
        this.lostBookCopies = lostBookCopies;
    }
}
