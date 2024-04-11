package com.library.warehouseservice.warehouseservice.repository;

import com.library.warehouseservice.warehouseservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface BookRepository extends JpaRepository<Book, Long> {
    public Book getBookByTitle(String title);
}
