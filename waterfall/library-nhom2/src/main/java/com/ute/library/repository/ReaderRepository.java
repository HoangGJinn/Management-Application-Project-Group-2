package com.ute.library.repository;

import com.ute.library.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReaderRepository extends JpaRepository<Reader, Integer> {
    Optional<Reader> findByReaderCode(String code);
    Optional<Reader> findByEmail(String email);
    Optional<Reader> findByPhone(String phone);
}
