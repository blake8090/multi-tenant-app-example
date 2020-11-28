package bke.multitenant.service;

import bke.multitenant.model.tenant.Book;
import bke.multitenant.repository.tenant.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        Iterable<Book> books = bookRepository.findAll();
        return StreamSupport
                .stream(books.spliterator(), false)
                .collect(Collectors.toList());
    }
}
