package com.management.coffee.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.coffee.model.CafeOrder;
import com.management.coffee.model.OrderItem;
import com.management.coffee.model.Product;
import com.management.coffee.model.User;
import com.management.coffee.repository.OrderItemRepository;
import com.management.coffee.repository.OrderRepository;
import com.management.coffee.repository.ProductRepository;
import com.management.coffee.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class PosController {

    private static final Logger log = LoggerFactory.getLogger(PosController.class);

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PosController(ProductRepository productRepository, UserRepository userRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping("/pos")
    public String posPage(Model model, HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null) return "redirect:/login";
        model.addAttribute("products", productRepository.findAll());
        return "pos";
    }

    @PostMapping("/pos/checkout")
    @Transactional
    public String checkout(@RequestParam String cartJson, HttpSession session) throws Exception {
        Object uid = session.getAttribute("userId");
        if (uid == null) return "redirect:/login";
        Integer staffId = (Integer) uid;
        User staff = userRepository.findById(staffId).orElse(null);
        List<CartItem> cart = objectMapper.readValue(cartJson, new TypeReference<List<CartItem>>(){});
        log.info("Checkout started: staffId={}, cartItems={}", staffId, cart.size());
        CafeOrder order = new CafeOrder();
        order.setStaff(staff);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cart) {
            Product p = productRepository.findById(ci.productId).orElse(null);
            if (p == null) continue;
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setQuantity(ci.quantity);
            oi.setSize(ci.size);
            oi.setIceLevel(ci.iceLevel);
            oi.setSugarLevel(ci.sugarLevel);
            oi.setTemperature(ci.temperature);
            order.getItems().add(oi);
            total = total.add(p.getBasePrice().multiply(BigDecimal.valueOf(ci.quantity)));
        }
        log.info("Checkout total calculated: staffId={}, totalAmount={}, cartItems={}", staffId, total, cart.size());
        order.setTotalAmount(total);
        order = orderRepository.save(order);
        log.info("Order saved successfully: orderId={}, staffId={}, totalAmount={}", order.getOrderId(), staffId, total);
        return "redirect:/pos";
    }

    public static class CartItem {
        public Integer productId;
        public Integer quantity;
        public String size;
        public String iceLevel;
        public String sugarLevel;
        public String temperature;
    }
}
