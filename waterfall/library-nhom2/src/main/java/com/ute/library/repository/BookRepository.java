package com.ute.library.repository;

import com.ute.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Optional<Book> findByBookCode(String code);
}
