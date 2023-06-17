package eu.lefler.library.controller;

import eu.lefler.library.entity.Reader;
import eu.lefler.library.repository.ReaderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {
    private final ReaderRepository repo;

    ReaderController(ReaderRepository repo) { this.repo = repo; }

    @GetMapping("")
    List<Reader> getAll() { return repo.findAll(); }

    @GetMapping("/{id}")
    Reader getReader(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found"));
    }

    @PostMapping("")
    Reader createReader(@RequestBody Reader newReader) {
        return repo.save(newReader);
    }

    @PostMapping("/{id}/subscription/extend")
    Reader extendReaderSubscription(@PathVariable Long id) {
        Reader reader = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found"));

        // check if the reader has active subscription and extend it by one year. If not, create a new one.
        if(reader.getSubscriptionExpirationDate() == null || reader.getSubscriptionExpirationDate().isBefore(LocalDateTime.now()))
            reader.setSubscriptionExpirationDate(LocalDateTime.now().plusYears(1));
        else {
            reader.setSubscriptionExpirationDate(reader.getSubscriptionExpirationDate().plusYears(1));
        }

        return repo.save(reader);
    }

    @PutMapping("/{id}")
    Reader updateReader(@PathVariable Long id, @RequestBody Reader newReader) {
        Reader updatedReader = repo.findById(id)
                .map(reader -> {
                    reader.setName(newReader.getName());
                    reader.setSurname(newReader.getSurname());
                    reader.setEmail(newReader.getEmail());
                    return repo.save(reader);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found"));

        return updatedReader;
    }

    @DeleteMapping("/{id}")
    void deleteReader(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
