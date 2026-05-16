package com.management.coffee.config;

import com.management.coffee.model.Category;
import com.management.coffee.model.Product;
import com.management.coffee.model.User;
import com.management.coffee.model.enums.Role;
import com.management.coffee.repository.CategoryRepository;
import com.management.coffee.repository.ProductRepository;
import com.management.coffee.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, CategoryRepository categoryRepository, ProductRepository productRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setFullName("Administrator");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            User staff = new User();
            staff.setUsername("staff");
            staff.setPasswordHash(passwordEncoder.encode("staff123"));
            staff.setFullName("Staff User");
            staff.setRole(Role.STAFF);
            userRepository.save(staff);
        }

        if (categoryRepository.count() == 0) {
            Category c1 = new Category(); c1.setCategoryName("Coffee"); c1.setDescription("Coffee drinks");
            Category c2 = new Category(); c2.setCategoryName("Tea"); c2.setDescription("Tea & others");
            categoryRepository.save(c1); categoryRepository.save(c2);

            Product p1 = new Product(); p1.setCategory(c1); p1.setProductName("Cappuccino"); p1.setBasePrice(new BigDecimal("80.00"));
            Product p2 = new Product(); p2.setCategory(c1); p2.setProductName("Espresso"); p2.setBasePrice(new BigDecimal("50.00"));
            Product p3 = new Product(); p3.setCategory(c2); p3.setProductName("Tea"); p3.setBasePrice(new BigDecimal("40.00"));
            productRepository.save(p1); productRepository.save(p2); productRepository.save(p3);
        }
    }
}
