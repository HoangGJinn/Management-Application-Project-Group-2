package com.management.coffee.config;

import com.management.coffee.model.Category;
import com.management.coffee.model.Product;
import com.management.coffee.model.User;
import com.management.coffee.model.enums.ProductStatus;
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

        Category coffee = ensureCategory("Coffee", "Espresso, brewed coffee, and milk coffee drinks");
        Category tea = ensureCategory("Tea", "Tea and herbal drinks");
        Category juice = ensureCategory("Juice", "Fresh fruit juices");
        Category cake = ensureCategory("Cake", "Cakes and sweet desserts");
        Category food = ensureCategory("Food", "Quick meals and savory snacks");
        Category other = ensureCategory("Other", "Other cafe menu items");

        ensureProduct(coffee, "Black Coffee", "Bold brewed coffee with a clean finish.", "3.75", "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=900&q=80", ProductStatus.ACTIVE);
        ensureProduct(coffee, "Espresso", "Pure, concentrated coffee served short.", "3.50", "https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04?auto=format&fit=crop&w=900&q=80", ProductStatus.ACTIVE);
        ensureProduct(coffee, "Cappuccino", "Espresso with steamed milk and dense foam.", "4.75", "https://images.unsplash.com/photo-1572442388796-11668a67e53d?auto=format&fit=crop&w=900&q=80", ProductStatus.ACTIVE);
        ensureProduct(tea, "Green Tea", "Light green tea with a soft herbal aroma.", "2.50", "https://images.unsplash.com/photo-1556881286-fc6915169721?auto=format&fit=crop&w=900&q=80", ProductStatus.ACTIVE);
        ensureProduct(juice, "Orange Juice", "Fresh orange juice served chilled.", "3.00", "https://images.unsplash.com/photo-1600271886742-f049cd451bba?auto=format&fit=crop&w=900&q=80", ProductStatus.ACTIVE);
        ensureProduct(cake, "Chocolate Cake", "Rich chocolate cake with cocoa cream.", "4.25", "https://images.unsplash.com/photo-1606890737304-57a1ca8a5b62?auto=format&fit=crop&w=900&q=80", ProductStatus.OUT_OF_STOCK);
        ensureProduct(food, "Croissant Sandwich", "Buttery croissant with ham and cheese.", "5.20", "https://images.unsplash.com/photo-1608198093002-ad4e005484ec?auto=format&fit=crop&w=900&q=80", ProductStatus.ACTIVE);
        ensureProduct(other, "Mineral Water", "Bottled mineral water.", "1.50", "https://images.unsplash.com/photo-1548839140-29a749e1cf4d?auto=format&fit=crop&w=900&q=80", ProductStatus.ACTIVE);
    }

    private Category ensureCategory(String name, String description) {
        return categoryRepository.findByCategoryName(name)
                .orElseGet(() -> categoryRepository.save(createCategory(name, description)));
    }

    private void ensureProduct(Category category, String name, String description, String price, String imageUrl, ProductStatus status) {
        if (productRepository.findByProductName(name).isEmpty()) {
            productRepository.save(createProduct(category, name, description, price, imageUrl, status));
        }
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setCategoryName(name);
        category.setDescription(description);
        return category;
    }

    private Product createProduct(Category category, String name, String description, String price, String imageUrl, ProductStatus status) {
        Product product = new Product();
        product.setCategory(category);
        product.setProductName(name);
        product.setDescription(description);
        product.setBasePrice(new BigDecimal(price));
        product.setImageUrl(imageUrl);
        product.setStatus(status);
        return product;
    }
}
