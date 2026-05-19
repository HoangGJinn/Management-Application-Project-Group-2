package com.management.coffee.controller;

import com.management.coffee.model.Category;
import com.management.coffee.model.Product;
import com.management.coffee.model.enums.ProductStatus;
import com.management.coffee.repository.CategoryRepository;
import com.management.coffee.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class MenuController {

    private static final BigDecimal SMALL_PRICE_THRESHOLD = new BigDecimal("1000");
    private static final BigDecimal VND_THOUSAND = new BigDecimal("1000");

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public MenuController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/menu")
    public String menuPage(
            @RequestParam(value = "category", required = false) String categoryName,
            @RequestParam(value = "search", required = false) String searchKeyword,
            Model model,
            HttpSession session) {

        Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(role.toString())) return "redirect:/login";

        // Get all categories
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        // Get products based on filters
        List<Product> products;
        if (categoryName != null && !categoryName.isEmpty() && !categoryName.equals("all")) {
            products = productRepository.findAll().stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getCategoryName().equals(categoryName))
                    .toList();
        } else {
            products = productRepository.findAll();
        }

        // Apply search filter
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            final String keyword = searchKeyword.toLowerCase();
            products = products.stream()
                    .filter(p -> p.getProductName().toLowerCase().contains(keyword) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(keyword)))
                    .toList();
        }

        model.addAttribute("products", products);
        model.addAttribute("currentCategory", categoryName != null ? categoryName : "all");
        model.addAttribute("searchKeyword", searchKeyword != null ? searchKeyword : "");

        return "menu";
    }

    @GetMapping("/menu/add")
    public String addProductPage(Model model, HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(role.toString())) return "redirect:/login";

        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("product", new Product());
        return "menu-form";
    }

    @PostMapping("/menu/add")
    public String addProduct(
            @RequestParam String productName,
            @RequestParam String description,
            @RequestParam BigDecimal basePrice,
            @RequestParam(required = false) String imageUrl,
            @RequestParam Integer categoryId,
            @RequestParam(required = false, defaultValue = "ACTIVE") String status,
            HttpSession session) {

        Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(role.toString())) return "redirect:/login";

        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) return "redirect:/menu";

        Product product = new Product();
        product.setProductName(productName);
        product.setDescription(description);
        product.setBasePrice(normalizeVndPrice(basePrice));
        product.setImageUrl(imageUrl != null ? imageUrl : "");
        product.setCategory(category.get());
        product.setStatus(ProductStatus.valueOf(status));

        productRepository.save(product);
        return "redirect:/menu";
    }

    @GetMapping("/menu/edit/{id}")
    public String editProductPage(@PathVariable Integer id, Model model, HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(role.toString())) return "redirect:/login";

        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) return "redirect:/menu";

        model.addAttribute("product", product.get());
        model.addAttribute("categories", categoryRepository.findAll());
        return "menu-form";
    }

    @PostMapping("/menu/edit/{id}")
    public String updateProduct(
            @PathVariable Integer id,
            @RequestParam String productName,
            @RequestParam String description,
            @RequestParam BigDecimal basePrice,
            @RequestParam(required = false) String imageUrl,
            @RequestParam Integer categoryId,
            @RequestParam(required = false, defaultValue = "ACTIVE") String status,
            HttpSession session) {

        Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(role.toString())) return "redirect:/login";

        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) return "redirect:/menu";

        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) return "redirect:/menu";

        Product p = product.get();
        p.setProductName(productName);
        p.setDescription(description);
        p.setBasePrice(normalizeVndPrice(basePrice));
        if (imageUrl != null && !imageUrl.isEmpty()) {
            p.setImageUrl(imageUrl);
        }
        p.setCategory(category.get());
        p.setStatus(ProductStatus.valueOf(status));

        productRepository.save(p);
        return "redirect:/menu";
    }

    @GetMapping("/menu/delete/{id}")
    public String deleteProduct(@PathVariable Integer id, HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null || !"ADMIN".equals(role.toString())) return "redirect:/login";

        productRepository.deleteById(id);
        return "redirect:/menu";
    }

    private BigDecimal normalizeVndPrice(BigDecimal price) {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        if (price.compareTo(BigDecimal.ZERO) > 0 && price.compareTo(SMALL_PRICE_THRESHOLD) < 0) {
            return price.multiply(VND_THOUSAND);
        }
        return price;
    }
}


