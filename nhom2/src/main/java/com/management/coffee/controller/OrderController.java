package com.management.coffee.controller;

import com.management.coffee.model.CafeOrder;
import com.management.coffee.model.Payment;
import com.management.coffee.model.enums.OrderStatus;
import com.management.coffee.model.enums.PaymentMethod;
import com.management.coffee.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {
    @Autowired
    private OrderService orderService;

    // 2. API Update trạng thái (Bếp / Nhân viên giao)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer id,
            @RequestParam OrderStatus status) {
        try {
            CafeOrder updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // 3. API Lấy danh sách đơn theo trạng thái để hiển thị trên UI
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CafeOrder>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<CafeOrder> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
}
