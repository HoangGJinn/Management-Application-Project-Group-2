package com.ute.library.repository;

import com.ute.library.model.BorrowItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowItemRepository extends JpaRepository<BorrowItem, Integer> {
	boolean existsByBook_Id(Integer bookId);
}
