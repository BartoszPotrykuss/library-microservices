package com.library.rentalservice.service;

import com.library.rentalservice.dto.BookTitle;
import com.library.rentalservice.entity.Rental;

import java.util.List;

public interface RentalService {
    List<Rental> getAllRentals();
    Rental rentBook(BookTitle bookTitle);
    Long returnBook(Long id);
}
