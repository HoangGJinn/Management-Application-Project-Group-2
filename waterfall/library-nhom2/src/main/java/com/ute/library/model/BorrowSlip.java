package com.ute.library.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "borrow_slips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowSlip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "slip_code", nullable = false, unique = true)
    private String slipCode;

    @ManyToOne
    @JoinColumn(name = "reader_id", nullable = false)
    private Reader reader;

    @ManyToOne
    @JoinColumn(name = "librarian_id", nullable = false)
    private Librarian librarian;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(nullable = false)
    private String status;

    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "borrowSlip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowItem> items;
}
