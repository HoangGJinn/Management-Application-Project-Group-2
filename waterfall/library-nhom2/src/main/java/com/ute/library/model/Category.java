package com.ute.library.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
