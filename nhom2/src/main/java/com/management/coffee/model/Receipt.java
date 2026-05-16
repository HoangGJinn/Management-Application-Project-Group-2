package com.management.coffee.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer receiptId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private CafeOrder order;

    @Column(name = "receipt_content", columnDefinition = "TEXT")
    private String receiptContent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Receipt() { this.createdAt = LocalDateTime.now(); }

    public Integer getReceiptId() { return receiptId; }
    public void setReceiptId(Integer receiptId) { this.receiptId = receiptId; }
    public CafeOrder getOrder() { return order; }
    public void setOrder(CafeOrder order) { this.order = order; }
    public String getReceiptContent() { return receiptContent; }
    public void setReceiptContent(String receiptContent) { this.receiptContent = receiptContent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
