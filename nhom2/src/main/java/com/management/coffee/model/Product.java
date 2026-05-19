package com.management.coffee.model;

import com.management.coffee.model.enums.ProductStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    private static final BigDecimal SMALL_PRICE_THRESHOLD = new BigDecimal("1000");
    private static final BigDecimal VND_THOUSAND = new BigDecimal("1000");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "product_name", length = 100)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVE','OUT_OF_STOCK','INACTIVE')")
    private ProductStatus status = ProductStatus.ACTIVE;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public BigDecimal getVndPrice() {
        if (basePrice != null && basePrice.compareTo(BigDecimal.ZERO) > 0 && basePrice.compareTo(SMALL_PRICE_THRESHOLD) < 0) {
            return basePrice.multiply(VND_THOUSAND);
        }
        return basePrice;
    }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
}
