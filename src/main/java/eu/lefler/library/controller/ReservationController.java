package eu.lefler.library.controller;

import eu.lefler.library.entity.Book;
import eu.lefler.library.entity.Reader;
import eu.lefler.library.entity.Reservation;
import eu.lefler.library.repository.BookRepository;
import eu.lefler.library.repository.ReaderRepository;
import eu.lefler.library.repository.ReservationRepository;
import eu.lefler.library.request.ReservationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationRepository repo;
    private final BookRepository bookRepo;
    private final ReaderRepository readerRepo;

    ReservationController(ReservationRepository repo, BookRepository bookRepo, ReaderRepository readerRepo) {
        this.repo = repo;
        this.bookRepo = bookRepo;
        this.readerRepo = readerRepo;
    }

    @GetMapping("")
    List<Reservation> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    Reservation getReservation(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
    }

    @GetMapping("/reader/{id}")
    List<Reservation> getReservationsByReader(@PathVariable Long id) {
        return repo.findByReaderId(id);
    }

    @GetMapping("/book/{id}")
    List<Reservation> getReservationsByBook(@PathVariable Long id) {
        return repo.findByBookId(id);
    }

    @PostMapping("")
    Reservation createReservation(@RequestBody ReservationRequest request) {
        Book book = bookRepo.findById(request.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        Reader reader = readerRepo.findById(request.getReaderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found"));

        // Check that the user doesn't already have a reservation for this book
        List<Reservation> reservations = repo.findByReaderIdAndBookId(reader.getId(), book.getId());
        if (!reservations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reader already has a reservation for this book");
        }

        Reservation reservation = new Reservation(book, reader, LocalDateTime.now());

        return repo.save(reservation);
    }

    @DeleteMapping("/{id}")
    void deleteReservation(@PathVariable Long id) {
        repo.deleteById(id);
    }

}
