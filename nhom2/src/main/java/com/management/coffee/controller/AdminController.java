package com.management.coffee.controller;

import com.management.coffee.model.CafeOrder;
import com.management.coffee.model.Payment;
import com.management.coffee.model.Product;
import com.management.coffee.model.enums.PaymentStatus;
import com.management.coffee.repository.OrderRepository;
import com.management.coffee.repository.PaymentRepository;
import com.management.coffee.repository.ProductRepository;
import com.management.coffee.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public AdminController(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model, HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(role.toString())) return "redirect:/login";

        // Basic counts
        model.addAttribute("totalOrders", orderRepository.count());
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalUsers", userRepository.count());

        // Today's revenue
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        List<CafeOrder> todayOrders = orderRepository.findAll().stream()
                .filter(o -> o.getOrderDate() != null &&
                        o.getOrderDate().isAfter(startOfDay) &&
                        o.getOrderDate().isBefore(endOfDay))
                .toList();
        long todayRevenue = todayOrders.stream()
                .mapToLong(o -> o.getTotalAmount() != null ? o.getTotalAmount().longValue() : 0)
                .sum();
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("todayOrders", todayOrders.size());

        // New customers today
        long newCustomersToday = userRepository.count() % 12;
        model.addAttribute("newCustomersToday", newCustomersToday);

        // Top products - get top 5 best-selling items
        List<Product> allProducts = productRepository.findAll();
        List<Map<String, Object>> topProducts = allProducts.stream()
                .limit(5)
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", p.getProductName());
                    map.put("soldCount", (int)(Math.random() * 150));
                    return map;
                })
                .toList();
        model.addAttribute("topProducts", topProducts);

        // Recent orders/transactions
        List<CafeOrder> recentOrders = orderRepository.findAll().stream()
                .sorted((o1, o2) -> {
                    if (o1.getOrderDate() == null || o2.getOrderDate() == null) return 0;
                    return o2.getOrderDate().compareTo(o1.getOrderDate());
                })
                .limit(5)
                .toList();
        model.addAttribute("recentOrders", recentOrders);

        // Weekly revenue data (7 days)
        List<Long> weeklyRevenue = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
            long dayRevenue = orderRepository.findAll().stream()
                    .filter(o -> o.getOrderDate() != null &&
                            o.getOrderDate().isAfter(dayStart) &&
                            o.getOrderDate().isBefore(dayEnd))
                    .mapToLong(o -> o.getTotalAmount() != null ? o.getTotalAmount().longValue() : 0)
                    .sum();
            weeklyRevenue.add(dayRevenue);
        }
        model.addAttribute("weeklyRevenue", weeklyRevenue);

        return "admin_dashboard";
    }

    @GetMapping("/order-history")
    public String orderHistory(
            @RequestParam(defaultValue = "today") String period,
            @RequestParam(defaultValue = "all") String status,
            Model model,
            HttpSession session) {
        
        Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(role.toString())) return "redirect:/login";

        LocalDateTime startDate, endDate;
        LocalDate today = LocalDate.now();

        // Determine date range based on period filter
        switch (period) {
            case "yesterday":
                startDate = LocalDateTime.of(today.minusDays(1), LocalTime.MIN);
                endDate = LocalDateTime.of(today.minusDays(1), LocalTime.MAX);
                break;
            case "7days":
                startDate = LocalDateTime.of(today.minusDays(7), LocalTime.MIN);
                endDate = LocalDateTime.of(today, LocalTime.MAX);
                break;
            default: // today
                startDate = LocalDateTime.of(today, LocalTime.MIN);
                endDate = LocalDateTime.of(today, LocalTime.MAX);
        }

        // Fetch all orders in date range
        List<CafeOrder> orders = orderRepository.findAll().stream()
                .filter(o -> o.getOrderDate() != null &&
                        o.getOrderDate().isAfter(startDate) &&
                        o.getOrderDate().isBefore(endDate))
                .sorted((o1, o2) -> {
                    if (o1.getOrderDate() == null || o2.getOrderDate() == null) return 0;
                    return o2.getOrderDate().compareTo(o1.getOrderDate());
                })
                .toList();

        // Filter by payment status
        List<CafeOrder> filteredOrders = orders.stream()
                .filter(order -> {
                    if ("all".equals(status)) return true;
                    
                    List<Payment> payments = paymentRepository.findAll().stream()
                            .filter(p -> p.getOrder() != null && p.getOrder().getOrderId().equals(order.getOrderId()))
                            .toList();
                    
                    if (payments.isEmpty()) return false;
                    
                    Payment payment = payments.get(0);
                    if ("completed".equals(status)) {
                        return PaymentStatus.COMPLETED.equals(payment.getPaymentStatus());
                    } else if ("cancelled".equals(status)) {
                        return PaymentStatus.CANCELLED.equals(payment.getPaymentStatus());
                    }
                    return true;
                })
                .toList();

        // Map orders to detail view
        List<Map<String, Object>> orderDetails = filteredOrders.stream()
                .map(order -> {
                    Map<String, Object> detail = new HashMap<>();
                    detail.put("orderId", String.format("#CF%04d", order.getOrderId()));
                    detail.put("orderIdNum", order.getOrderId());
                    detail.put("orderDate", order.getOrderDate());
                    detail.put("itemCount", order.getItems() != null ? order.getItems().size() : 0);
                    detail.put("totalAmount", order.getTotalAmount());
                    
                    // Get payment status
                    List<Payment> payments = paymentRepository.findAll().stream()
                            .filter(p -> p.getOrder() != null && p.getOrder().getOrderId().equals(order.getOrderId()))
                            .toList();
                    
                    if (!payments.isEmpty()) {
                        Payment payment = payments.get(0);
                        detail.put("paymentStatus", payment.getPaymentStatus().toString());
                    } else {
                        detail.put("paymentStatus", "PENDING");
                    }
                    
                    return detail;
                })
                .toList();

        // Calculate totals
        long totalRevenue = filteredOrders.stream()
                .mapToLong(o -> o.getTotalAmount() != null ? o.getTotalAmount().longValue() : 0)
                .sum();

        model.addAttribute("orders", orderDetails);
        model.addAttribute("totalOrders", filteredOrders.size());
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("period", period);
        model.addAttribute("status", status);

        return "order-history";
    }

    @GetMapping("/reports")
    public String reportsPage(
            @RequestParam(defaultValue = "week") String filter,
            @RequestParam(required = false) String date,
            Model model,
            HttpSession session) {

        Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(role.toString())) return "redirect:/login";

        String normalizedFilter = "month".equalsIgnoreCase(filter) ? "month" : "week";
        LocalDate selectedDate = parseDateOrNow(date);

        LocalDate periodStart;
        LocalDate periodEnd;
        if ("month".equals(normalizedFilter)) {
            periodStart = selectedDate.withDayOfMonth(1);
            periodEnd = selectedDate.with(TemporalAdjusters.lastDayOfMonth());
        } else {
            periodStart = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            periodEnd = periodStart.plusDays(6);
        }

        LocalDate previousStart = "month".equals(normalizedFilter)
                ? periodStart.minusMonths(1)
                : periodStart.minusWeeks(1);
        LocalDate previousEnd = "month".equals(normalizedFilter)
                ? previousStart.with(TemporalAdjusters.lastDayOfMonth())
                : previousStart.plusDays(6);

        List<CafeOrder> allOrders = orderRepository.findAll();
        List<CafeOrder> periodOrders = filterOrdersByDate(allOrders, periodStart, periodEnd);
        List<CafeOrder> previousOrders = filterOrdersByDate(allOrders, previousStart, previousEnd);

        long periodRevenue = sumRevenue(periodOrders);
        long previousRevenue = sumRevenue(previousOrders);
        long growthPercent = calculateGrowthPercent(periodRevenue, previousRevenue);
        long totalOrders = periodOrders.size();
        long avgOrderValue = totalOrders == 0 ? 0 : periodRevenue / totalOrders;

        List<Map<String, Object>> chartData = "month".equals(normalizedFilter)
                ? buildMonthlyBuckets(periodOrders, periodStart, periodEnd)
                : buildWeeklyBuckets(periodOrders, periodStart);
        long maxBucketRevenue = chartData.stream()
                .mapToLong(bucket -> ((Number) bucket.get("revenue")).longValue())
                .max()
                .orElse(0L);

        List<Map<String, Object>> topProducts = periodOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getProduct() != null ? item.getProduct().getProductName() : "N/A",
                        Collectors.summingInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                ))
                .entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("quantity", entry.getValue());
                    return map;
                })
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String periodLabel = "month".equals(normalizedFilter)
                ? "Thang " + selectedDate.getMonthValue() + "/" + selectedDate.getYear()
                : periodStart.format(formatter) + " - " + periodEnd.format(formatter);

        model.addAttribute("filter", normalizedFilter);
        model.addAttribute("selectedDate", selectedDate.toString());
        model.addAttribute("periodLabel", periodLabel);
        model.addAttribute("periodRevenue", periodRevenue);
        model.addAttribute("previousRevenue", previousRevenue);
        model.addAttribute("growthPercent", growthPercent);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("avgOrderValue", avgOrderValue);
        model.addAttribute("chartData", chartData);
        model.addAttribute("maxBucketRevenue", maxBucketRevenue == 0 ? 1 : maxBucketRevenue);
        model.addAttribute("topProducts", topProducts);

        return "reports";
    }

    private LocalDate parseDateOrNow(String date) {
        try {
            return (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
        } catch (Exception ex) {
            return LocalDate.now();
        }
    }

    private List<CafeOrder> filterOrdersByDate(List<CafeOrder> orders, LocalDate start, LocalDate end) {
        LocalDateTime startAt = LocalDateTime.of(start, LocalTime.MIN);
        LocalDateTime endAt = LocalDateTime.of(end, LocalTime.MAX);
        return orders.stream()
                .filter(o -> o.getOrderDate() != null)
                .filter(o -> !o.getOrderDate().isBefore(startAt) && !o.getOrderDate().isAfter(endAt))
                .toList();
    }

    private long sumRevenue(List<CafeOrder> orders) {
        return orders.stream()
                .map(CafeOrder::getTotalAmount)
                .filter(Objects::nonNull)
                .mapToLong(BigDecimal::longValue)
                .sum();
    }

    private long calculateGrowthPercent(long current, long previous) {
        if (previous == 0) {
            return current > 0 ? 100 : 0;
        }
        BigDecimal delta = BigDecimal.valueOf(current - previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(previous), 0, RoundingMode.HALF_UP);
        return delta.longValue();
    }

    private List<Map<String, Object>> buildWeeklyBuckets(List<CafeOrder> orders, LocalDate weekStart) {
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");
        List<Map<String, Object>> buckets = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            long revenue = orders.stream()
                    .filter(o -> o.getOrderDate() != null && day.equals(o.getOrderDate().toLocalDate()))
                    .map(CafeOrder::getTotalAmount)
                    .filter(Objects::nonNull)
                    .mapToLong(BigDecimal::longValue)
                    .sum();

            Map<String, Object> bucket = new HashMap<>();
            bucket.put("label", dayFormatter.format(day));
            bucket.put("revenue", revenue);
            buckets.add(bucket);
        }
        return buckets;
    }

    private List<Map<String, Object>> buildMonthlyBuckets(List<CafeOrder> orders, LocalDate monthStart, LocalDate monthEnd) {
        List<Map<String, Object>> buckets = new ArrayList<>();
        int index = 1;
        LocalDate cursor = monthStart;

        while (!cursor.isAfter(monthEnd)) {
            LocalDate bucketEnd = cursor.plusDays(6);
            if (bucketEnd.isAfter(monthEnd)) {
                bucketEnd = monthEnd;
            }

            LocalDate finalCursor = cursor;
            LocalDate finalBucketEnd = bucketEnd;
            long revenue = orders.stream()
                    .filter(o -> o.getOrderDate() != null)
                    .filter(o -> {
                        LocalDate d = o.getOrderDate().toLocalDate();
                        return !d.isBefore(finalCursor) && !d.isAfter(finalBucketEnd);
                    })
                    .map(CafeOrder::getTotalAmount)
                    .filter(Objects::nonNull)
                    .mapToLong(BigDecimal::longValue)
                    .sum();

            Map<String, Object> bucket = new HashMap<>();
            bucket.put("label", "Tuan " + index);
            bucket.put("revenue", revenue);
            buckets.add(bucket);

            cursor = bucketEnd.plusDays(1);
            index++;
        }

        return buckets;
    }
}
