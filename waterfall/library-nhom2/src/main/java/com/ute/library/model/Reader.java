package com.ute.library.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "readers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reader_code", nullable = false, unique = true)
    private String readerCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String address;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
