package com.library.warehouseservice.warehouseservice.controller;

import com.library.warehouseservice.warehouseservice.dto.BookResponse;
import com.library.warehouseservice.warehouseservice.model.Book;
import com.library.warehouseservice.warehouseservice.service.BookServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {

    private final BookServiceImpl bookService;

    public BookController(BookServiceImpl bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book newBook = bookService.addBook(book);
        return new ResponseEntity<>(newBook, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{title}")
    public ResponseEntity<BookResponse> getBookByTitle(@PathVariable String title) {
        BookResponse bookResponse = bookService.getBookByTitle(title);
        return new ResponseEntity<>(bookResponse ,HttpStatus.OK);
    }

    @PatchMapping("/{title}/removeQuantity")
    public ResponseEntity<String> removeOneQuantity(@PathVariable String title) {
        bookService.removeOneQuantity(title);
        return ResponseEntity.ok("The book has been rented");
    }

    @PatchMapping("/{title}/addQuantity")
    public ResponseEntity<String> addOneQuantity(@PathVariable String title) {
        bookService.addOneQuantity(title);
        return ResponseEntity.ok("The book has been returned");
    }
}
