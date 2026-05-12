package com.ute.library.repository;

import com.ute.library.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
    Optional<Publisher> findByPublisherName(String name);
}
