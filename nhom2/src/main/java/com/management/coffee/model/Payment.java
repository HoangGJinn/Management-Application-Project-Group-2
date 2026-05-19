package com.management.coffee.model;

import com.management.coffee.model.enums.PaymentMethod;
import com.management.coffee.model.enums.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private CafeOrder order;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", columnDefinition = "ENUM('CASH','VNPAY')")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", columnDefinition = "ENUM('PENDING','COMPLETED','CANCELLED')")
    private PaymentStatus paymentStatus = PaymentStatus.COMPLETED;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    public Payment() {
        this.paymentDate = LocalDateTime.now();
    }

    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }
    public CafeOrder getOrder() { return order; }
    public void setOrder(CafeOrder order) { this.order = order; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
}
