package com.library.rentalservice.controller;

import com.library.rentalservice.dto.BookTitle;
import com.library.rentalservice.entity.Rental;
import com.library.rentalservice.service.RentalService;
import com.library.rentalservice.service.RentalServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RentalController {

    private final RentalService rentalService;
    public RentalController(RentalServiceImpl rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping("/rent")
    public ResponseEntity<List<Rental>> getAllRentals() {
        List<Rental> rentals = rentalService.getAllRentals();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rentals);
    }

    @PostMapping("/rent")
    public ResponseEntity<?> rentBook(@RequestBody BookTitle bookTitle) {
       Rental rental = rentalService.rentBook(bookTitle);
        return ResponseEntity.ok(rental);
    }

    @DeleteMapping("/return/{id}")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
            Long additionalFee = rentalService.returnBook(id);
            String message;
            if (additionalFee.equals(0L)) {
                message = "Oddano książkę w terminie.";
            }
            else {
                message = "Oddano książkę "+ additionalFee + " dni po terminie. Kwota do zapłacenia: " + additionalFee + " PLN";
            }
            return ResponseEntity.ok(message);

    }
}
