package com.management.coffee.service;

import com.management.coffee.model.CafeOrder;
import com.management.coffee.model.Payment;
import com.management.coffee.model.enums.OrderStatus;
import com.management.coffee.model.enums.PaymentMethod;
import com.management.coffee.model.enums.PaymentStatus;
import com.management.coffee.repository.OrderRepository;
import com.management.coffee.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository; // Interface extends JpaRepository<Payment, Integer>
    public Payment processCheckout(Integer orderId, PaymentMethod method) {
        CafeOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        // 1. Lưu thông tin thanh toán (Tạo record trong bảng payments)
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(PaymentStatus.COMPLETED); // Nhân viên đã xác nhận thu tiền
        payment.setPaymentDate(LocalDateTime.now());

        // 2. Chuyển trạng thái đơn hàng (Bảng orders)
        // Vì DB của bạn mặc định Order tạo ra là 'Preparing', nên khúc này gán lại cho chắc
        order.setOrderStatus(OrderStatus.Preparing);
        orderRepository.save(order);

        return paymentRepository.save(payment);
    }

    // Hàm update trạng thái đơn hàng (dùng cho Màn hình Bếp / Giao món)
    public CafeOrder updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        CafeOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        order.setOrderStatus(newStatus);
        return orderRepository.save(order);
    }

    public List<CafeOrder> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }
}
