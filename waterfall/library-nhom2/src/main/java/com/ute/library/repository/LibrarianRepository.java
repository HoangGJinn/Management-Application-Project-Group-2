package com.ute.library.repository;

import com.ute.library.model.Librarian;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LibrarianRepository extends JpaRepository<Librarian, Integer> {
    Optional<Librarian> findByUsername(String username);
    Optional<Librarian> findByLibrarianCode(String code);
}
