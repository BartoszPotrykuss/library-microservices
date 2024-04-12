package com.library.rentalservice.repository;

import com.library.rentalservice.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    Optional<Rental> findByUsernameAndBookTitle(String username, String bookTitle);
    void deleteByUsernameAndBookTitle(String username, String bookTitle);
}
