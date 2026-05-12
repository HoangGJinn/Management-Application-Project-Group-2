package com.ute.library.controller;

import com.ute.library.model.Librarian;
import com.ute.library.repository.LibrarianRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class LoginController {
    
    @Autowired
    private LibrarianRepository librarianRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @GetMapping("/login")
    public ModelAndView showLoginPage() {
        return new ModelAndView("login");
    }
    
    @PostMapping("/api/login")
    @ResponseBody
    public Map<String, Object> login(
            @RequestBody Map<String, String> payload,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        String username = payload.get("username");
        String password = payload.get("password");
        
        if (username == null || password == null) {
            response.put("success", false);
            response.put("message", "Username and password are required");
            return response;
        }
        
        Optional<Librarian> librarianOpt = librarianRepository.findByUsername(username);
        
        if (librarianOpt.isPresent()) {
            Librarian librarian = librarianOpt.get();
            // Verify password
            if (passwordEncoder.matches(password, librarian.getPasswordHash())) {
                // Store in session
                session.setAttribute("librarianId", librarian.getId());
                session.setAttribute("username", librarian.getUsername());
                session.setAttribute("fullName", librarian.getFullName());
                
                response.put("success", true);
                response.put("message", "Login successful");
            } else {
                response.put("success", false);
                response.put("message", "Invalid password");
            }
        } else {
            response.put("success", false);
            response.put("message", "User not found");
        }
        
        return response;
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
