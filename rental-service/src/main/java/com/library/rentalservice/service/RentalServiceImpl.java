package com.library.rentalservice.service;

import com.library.rentalservice.dto.BookResponse;
import com.library.rentalservice.dto.BookTitle;
import com.library.rentalservice.entity.Rental;
import com.library.rentalservice.repository.RentalRepository;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final WebClient webClient;

    public RentalServiceImpl(RentalRepository rentalRepository, WebClient webClient) {
        this.rentalRepository = rentalRepository;
        this.webClient = webClient;
    }

    @Override
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    @Override
    public Rental rentBook(BookTitle bookTitle)  {
        Rental rental = new Rental();
        String title = bookTitle.getTitle();

        BookResponse bookResponse = webClient
                .get()
                .uri("http://localhost:8080/api/book/" + title)
                .retrieve()
                .bodyToMono(BookResponse.class)
                .block();

        if (bookResponse == null) {
            throw new IllegalArgumentException("Book not found");
        } else {
            if (bookResponse.getQuantity().equals(0L)) {
                throw new IllegalArgumentException("Every book titled "+ bookResponse.getTitle() +" has already been borrowed. Try again later");
            }
            else {
                rental.setBookTitle(bookResponse.getTitle());
                webClient
                        .method(HttpMethod.PATCH)
                        .uri("http://localhost:8080/api/book/" + title + "/removeQuantity")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                return rentalRepository.save(rental);
            }
        }
    }

    @Override
    public Long returnBook(Long id) {
        Optional<Rental> rentalOptional = rentalRepository.findById(id);
        Rental rental = rentalOptional.orElse(null);
        if (rental == null) {
            throw new IllegalArgumentException("Rental not found");
        }
        else {
            Long additionalFee = 0L;
            if(isDeadLineExceeded(rental)) {
                additionalFee = ChronoUnit.DAYS.between(rental.getEndDate(), LocalDate.now());
            }
            rentalRepository.deleteById(id);
            webClient
                    .method(HttpMethod.PATCH)
                    .uri("http://localhost:8080/api/book/" + rental.getBookTitle() + "/addQuantity")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return additionalFee;
        }
    }

    private boolean isDeadLineExceeded(Rental rental) {
        return LocalDate.now().isAfter(rental.getEndDate());
    }
}
