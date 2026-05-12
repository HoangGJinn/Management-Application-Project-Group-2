package com.ute.library.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "borrow_slip_id", nullable = false)
    private BorrowSlip borrowSlip;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private Integer quantity;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "item_status", nullable = false)
    private String itemStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
