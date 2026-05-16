package com.management.coffee.controller;

import com.management.coffee.model.User;
import com.management.coffee.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping({"/", "/login"})
    public String loginPage() { return "login"; }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        log.info("Login attempt received: username='{}', usernameLength={}", username, username != null ? username.length() : 0);
        var opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) {
            log.warn("Login failed: user not found for username='{}'", username);
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
        User user = opt.get();
        boolean passwordMatches = passwordEncoder.matches(password, user.getPasswordHash());
        log.info("Login lookup succeeded: userId={}, username='{}', role={}, passwordHashPresent={}, passwordMatches={}",
                user.getUserId(), user.getUsername(), user.getRole(), user.getPasswordHash() != null, passwordMatches);
        if (!passwordMatches) {
            log.warn("Login failed: password mismatch for username='{}', userId={}", username, user.getUserId());
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
        log.info("Login success: userId={}, username='{}', role={}", user.getUserId(), user.getUsername(), user.getRole());
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("role", user.getRole().name());
        if (user.getRole().name().equals("ADMIN")) return "redirect:/admin";
        return "redirect:/pos";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
