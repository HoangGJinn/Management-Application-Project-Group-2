package com.management.coffee.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.coffee.model.CafeOrder;
import com.management.coffee.model.OrderItem;
import com.management.coffee.model.Payment;
import com.management.coffee.model.Product;
import com.management.coffee.model.User;
import com.management.coffee.model.enums.PaymentMethod;
import com.management.coffee.model.enums.PaymentStatus;
import com.management.coffee.repository.CategoryRepository;
import com.management.coffee.repository.OrderItemRepository;
import com.management.coffee.repository.OrderRepository;
import com.management.coffee.repository.PaymentRepository;
import com.management.coffee.repository.ProductRepository;
import com.management.coffee.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class PosController {

    private static final Logger log = LoggerFactory.getLogger(PosController.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PosController(ProductRepository productRepository, CategoryRepository categoryRepository,
                         UserRepository userRepository, OrderRepository orderRepository,
                         OrderItemRepository orderItemRepository, PaymentRepository paymentRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/pos")
    public String posPage(Model model, HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null) return "redirect:/login";
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "pos";
    }

    @GetMapping("/pos_menu")
    public String posMenuPage(Model model, HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null) return "redirect:/login";
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "pos_menu";
    }

    @GetMapping("/pos_order")
    public String posOrderPage(HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null) return "redirect:/login";
        return "pos_order";
    }

    @PostMapping("/pos/checkout")
    @Transactional
    public String checkout(@RequestParam String cartJson,
                           @RequestParam(defaultValue = "CASH") String paymentMethod,
                           HttpSession session) throws Exception {
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

            BigDecimal itemPrice = p.getBasePrice();
            total = total.add(itemPrice.multiply(BigDecimal.valueOf(ci.quantity)));
        }

        log.info("Checkout total calculated: staffId={}, totalAmount={}, cartItems={}", staffId, total, cart.size());
        order.setTotalAmount(total);
        order = orderRepository.save(order);
        log.info("Order saved successfully: orderId={}, staffId={}, totalAmount={}", order.getOrderId(), staffId, total);

        // Create payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        try {
            payment.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
        } catch (IllegalArgumentException e) {
            payment.setPaymentMethod(PaymentMethod.CASH);
        }
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
        log.info("Payment saved: orderId={}, method={}", order.getOrderId(), paymentMethod);

        return "redirect:/pos?success=true";
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
