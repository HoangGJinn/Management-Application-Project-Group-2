package com.management.coffee.repository;

import com.management.coffee.model.CafeOrder;
import com.management.coffee.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<CafeOrder, Integer> {

    List<CafeOrder> findByOrderStatus(OrderStatus status);
}
