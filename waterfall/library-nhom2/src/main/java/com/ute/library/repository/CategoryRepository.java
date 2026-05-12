package com.ute.library.repository;

import com.ute.library.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByCategoryName(String name);
}
