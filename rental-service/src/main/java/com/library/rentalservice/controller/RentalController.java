package com.library.rentalservice.controller;

import com.library.rentalservice.dto.BookRequest;
import com.library.rentalservice.entity.Rental;
import com.library.rentalservice.service.RentalService;
import com.library.rentalservice.service.RentalServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
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
    public ResponseEntity<?> rentBook(@RequestBody BookRequest bookRequest, HttpServletRequest request) {
       Rental rental = rentalService.rentBook(bookRequest, request);
        return ResponseEntity.ok(rental);
    }

    @DeleteMapping("/return/{title}")
    public ResponseEntity<?> returnBook(@PathVariable String title, HttpServletRequest request) {
            Long additionalFee = rentalService.returnBook(title, request);
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
