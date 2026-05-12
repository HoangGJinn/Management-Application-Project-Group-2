package com.ute.library.repository;

import com.ute.library.model.BorrowItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BorrowItemRepository extends JpaRepository<BorrowItem, Integer> {
	boolean existsByBook_Id(Integer bookId);

	List<BorrowItem> findByItemStatusAndBorrowSlip_StatusOrderByBorrowSlip_DueDateAscIdDesc(String itemStatus, String slipStatus);

	List<BorrowItem> findByItemStatusAndBorrowSlip_StatusAndBorrowSlip_DueDateBeforeOrderByBorrowSlip_DueDateAscIdDesc(
			String itemStatus,
			String slipStatus,
			LocalDate dueDate
	);

	boolean existsByBorrowSlip_IdAndItemStatus(Integer borrowSlipId, String itemStatus);
}
