package com.ute.library.dto;

public class TimeStatisticsDTO {
    private long createdSlipCount;
    private long borrowedQuantity;
    private long returnedQuantity;
    private long overdueQuantity;
    private long completedSlipCount;
    private long borrowingSlipCount;

    public TimeStatisticsDTO() {
    }

    public TimeStatisticsDTO(long createdSlipCount, long borrowedQuantity, long returnedQuantity,
                             long overdueQuantity, long completedSlipCount, long borrowingSlipCount) {
        this.createdSlipCount = createdSlipCount;
        this.borrowedQuantity = borrowedQuantity;
        this.returnedQuantity = returnedQuantity;
        this.overdueQuantity = overdueQuantity;
        this.completedSlipCount = completedSlipCount;
        this.borrowingSlipCount = borrowingSlipCount;
    }

    public long getCreatedSlipCount() {
        return createdSlipCount;
    }

    public void setCreatedSlipCount(long createdSlipCount) {
        this.createdSlipCount = createdSlipCount;
    }

    public long getBorrowedQuantity() {
        return borrowedQuantity;
    }

    public void setBorrowedQuantity(long borrowedQuantity) {
        this.borrowedQuantity = borrowedQuantity;
    }

    public long getReturnedQuantity() {
        return returnedQuantity;
    }

    public void setReturnedQuantity(long returnedQuantity) {
        this.returnedQuantity = returnedQuantity;
    }

    public long getOverdueQuantity() {
        return overdueQuantity;
    }

    public void setOverdueQuantity(long overdueQuantity) {
        this.overdueQuantity = overdueQuantity;
    }

    public long getCompletedSlipCount() {
        return completedSlipCount;
    }

    public void setCompletedSlipCount(long completedSlipCount) {
        this.completedSlipCount = completedSlipCount;
    }

    public long getBorrowingSlipCount() {
        return borrowingSlipCount;
    }

    public void setBorrowingSlipCount(long borrowingSlipCount) {
        this.borrowingSlipCount = borrowingSlipCount;
    }
}
