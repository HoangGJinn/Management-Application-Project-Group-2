package com.ute.library.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "publishers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "publisher_name", nullable = false, unique = true)
    private String publisherName;

    private String address;

    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
