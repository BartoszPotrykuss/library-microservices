package com.library.rentalservice.service;

import com.library.rentalservice.dto.BookResponse;
import com.library.rentalservice.dto.BookRequest;
import com.library.rentalservice.entity.Rental;
import com.library.rentalservice.repository.RentalRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final WebClient webClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RentalServiceImpl(RentalRepository rentalRepository, WebClient webClient, KafkaTemplate<String, Object> kafkaTemplate) {
        this.rentalRepository = rentalRepository;
        this.webClient = webClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    @Override
    public Rental rentBook(BookRequest bookRequest, HttpServletRequest request)  {
        Rental rental = new Rental();
        String title = bookRequest.getTitle();

        //String token = authorizationHeader.substring(7);
//        String[] chunks = token.split("\\.");
//        Base64.Decoder decoder = Base64.getUrlDecoder();
//        String payload = new String(decoder.decode(chunks[1]));
//        rental.setUsername(payload);

        String authorizationHeader = request.getHeader("Authorization");
        String username = getSubjectFromJwtToken(authorizationHeader);


        Optional<Rental> optionalRental = rentalRepository.findByUsernameAndBookTitle(username, title);
        if (optionalRental.isPresent()) {
            throw new RuntimeException("You've already borrowed this book");
        }
        rental.setUsername(username);


        BookResponse bookResponse = webClient
                .get()
                .uri("http://localhost:8080/api/book/" + title)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
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

                /*
                webClient
                        .method(HttpMethod.PATCH)
                        .uri("http://localhost:8080/api/book/" + title + "/removeQuantity")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                */
                kafkaTemplate.send("removeQuantity", title);
                return rentalRepository.save(rental);
            }
        }
    }

    @Override
    @Transactional
    public Long returnBook(String title, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String username = getSubjectFromJwtToken(authorizationHeader);

        Optional<Rental> rentalOptional = rentalRepository.findByUsernameAndBookTitle(username, title);
        Rental rental = rentalOptional.orElse(null);


        if (rental == null) {
            throw new IllegalArgumentException("Rental not found");
        }
        else {
            long additionalFee = 0L;
            if(isDeadLineExceeded(rental)) {
                additionalFee = ChronoUnit.DAYS.between(rental.getEndDate(), LocalDate.now());
            }
            /*
            webClient
                    .method(HttpMethod.PATCH)
                    .uri("http://localhost:8080/api/book/" + rental.getBookTitle() + "/addQuantity")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
             */
            kafkaTemplate.send("addQuantity", title);
            rentalRepository.deleteByUsernameAndBookTitle(username, title);
            return additionalFee;
        }
    }

    private boolean isDeadLineExceeded(Rental rental) {
        return LocalDate.now().isAfter(rental.getEndDate());
    }

    private String getSubjectFromJwtToken(String authorizationHeader) {

        // Wyciągnij sam token JWT z nagłówka
        String jwtToken = authorizationHeader.substring(7);

        // Odczytaj token i pobierz z niego dane
        Claims claims = Jwts.parser().setSigningKey("5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437").parseClaimsJws(jwtToken).getBody();

        // Pobierz identyfikator użytkownika z tokena JWT
        return claims.getSubject();
    }
}
