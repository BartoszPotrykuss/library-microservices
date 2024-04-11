package com.library.warehouseservice.warehouseservice.service;

import com.library.warehouseservice.warehouseservice.dto.BookResponse;
import com.library.warehouseservice.warehouseservice.model.Book;
import com.library.warehouseservice.warehouseservice.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book addBook(Book book) {

        Book bookDB = bookRepository.getBookByTitle(book.getTitle());

        if (bookDB != null) {
            bookDB.setQuantity(book.getQuantity() + bookDB.getQuantity());
            return bookRepository.save(bookDB);
        }
        else return bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public BookResponse getBookByTitle(String title) {
        Book book = bookRepository.getBookByTitle(title);
        if (book != null) {
            BookResponse bookResponse = BookResponse.builder()
                    .title(book.getTitle())
                    .quantity(book.getQuantity())
                    .build();
            return bookResponse;
        }
        else {
            return null;
        }
    }

    @Override
    public void removeOneQuantity(String title) {
        Book bookDB = bookRepository.getBookByTitle(title);
        bookDB.setQuantity(bookDB.getQuantity() - 1);
        bookRepository.save(bookDB);
    }

    @Override
    public void addOneQuantity(String title) {
        Book bookDB = bookRepository.getBookByTitle(title);
        bookDB.setQuantity(bookDB.getQuantity() + 1);
        bookRepository.save(bookDB);
    }
}
