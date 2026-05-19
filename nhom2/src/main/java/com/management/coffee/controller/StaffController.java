package com.management.coffee.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffController {

    @GetMapping("/kitchen")
    public String kitchenPage(HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null) return "redirect:/login";
        return "kitchen";
    }

    @GetMapping("/pickup")
    public String pickupPage(HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null) return "redirect:/login";
        return "pickup";
    }
}
