package eu.lefler.library.controller;

import eu.lefler.library.entity.Book;
import eu.lefler.library.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookRepository repo;

    BookController(BookRepository repo) {
        this.repo = repo;
    }

    @GetMapping("")
    List<Book> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    Book getBook(@PathVariable Long id) {
        Book response = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return response;
    }

    @PostMapping("")
    Book createBook(@RequestBody Book newBook) {
        return repo.save(newBook);
    }

    @PutMapping("/{id}")
    Book updateBook(@PathVariable Long id, @RequestBody Book newBook) {
        Book updatedBook = repo.findById(id)
                .map(book -> {
                    book.setName(newBook.getName());
                    book.setAuthor(newBook.getAuthor());
                    return repo.save(book);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        return updatedBook;
    }

    @DeleteMapping("/{id}")
    void deleteBook(@PathVariable Long id) {
        repo.deleteById(id);
    }


}
