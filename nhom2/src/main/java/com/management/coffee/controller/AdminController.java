package com.management.coffee.controller;

import com.management.coffee.repository.OrderRepository;
import com.management.coffee.repository.ProductRepository;
import com.management.coffee.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public AdminController(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model, HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(role.toString())) return "redirect:/login";
        model.addAttribute("totalOrders", orderRepository.count());
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        return "admin_dashboard";
    }
}
