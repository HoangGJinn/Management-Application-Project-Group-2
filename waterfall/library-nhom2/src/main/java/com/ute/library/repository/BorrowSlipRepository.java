package com.ute.library.repository;

import com.ute.library.model.BorrowSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BorrowSlipRepository extends JpaRepository<BorrowSlip, Integer> {
    Optional<BorrowSlip> findBySlipCode(String code);
}
