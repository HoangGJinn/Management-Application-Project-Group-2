package com.management.coffee.repository;

import com.management.coffee.model.CafeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CafeOrder, Integer> {
}
