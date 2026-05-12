package com.ute.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

@Controller
public class UiController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping({"/index", "/home"})
    public String index() {
        return "index";
    }



    @GetMapping("/loans-manage")
    public String loansManage() {
        return "loans-manage";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }

    // -- Mock API endpoints (returning static mock data) --
    @GetMapping("/api/books")
    @ResponseBody
    public List<Map<String, Object>> apiBooks() {
        Map<String, Object> b1 = new HashMap<>();
        b1.put("id", 1);
        b1.put("title", "Introduction to Java");
        b1.put("author", "Nguyen Van A");
        b1.put("status", "AVAILABLE");

        Map<String, Object> b2 = new HashMap<>();
        b2.put("id", 2);
        b2.put("title", "Database Systems");
        b2.put("author", "Tran Thi B");
        b2.put("status", "BORROWED");

        return Arrays.asList(b1, b2);
    }

    @GetMapping("/api/members")
    @ResponseBody
    public List<Map<String, Object>> apiMembers() {
        Map<String, Object> m1 = new HashMap<>();
        m1.put("id", 1);
        m1.put("name", "Le Van C");
        m1.put("email", "levan.c@example.com");

        Map<String, Object> m2 = new HashMap<>();
        m2.put("id", 2);
        m2.put("name", "Pham Thi D");
        m2.put("email", "pham.t.d@example.com");

        return Arrays.asList(m1, m2);
    }

    @GetMapping("/api/loans")
    @ResponseBody
    public List<Map<String, Object>> apiLoans() {
        Map<String, Object> l1 = new HashMap<>();
        l1.put("id", 1);
        l1.put("bookTitle", "Database Systems");
        l1.put("memberName", "Le Van C");
        l1.put("dueDate", "2026-06-01");

        return Arrays.asList(l1);
    }

    @GetMapping("/api/dashboard/stats")
    @ResponseBody
    public Map<String, Object> dashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMembers", 1223);
        stats.put("borrowedBooks", 740);
        stats.put("overdueBooks", 22);
        stats.put("newMembers", 60);
        return stats;
    }

    // Placeholders for modifying data — currently return 501-like messages via simple strings
    @PostMapping("/api/books/{id}/borrow")
    @ResponseBody
    public Map<String, Object> borrowBook(@PathVariable("id") Long id) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "TODO");
        resp.put("message", "Implement borrowBook(id) to update database or call service");
        resp.put("bookId", id);
        return resp;
    }

    @PostMapping("/api/books")
    @ResponseBody
    public Map<String, Object> createBook(@RequestBody Map<String, Object> book) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "TODO");
        resp.put("message", "Implement createBook(book) to persist a new book");
        resp.put("received", book);
        return resp;
    }

}
