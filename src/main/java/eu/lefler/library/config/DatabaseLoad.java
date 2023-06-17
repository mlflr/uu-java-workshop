package eu.lefler.library.config;

import eu.lefler.library.entity.Book;
import eu.lefler.library.entity.Loan;
import eu.lefler.library.entity.Reader;
import eu.lefler.library.entity.Reservation;
import eu.lefler.library.repository.BookRepository;
import eu.lefler.library.repository.LoanRepository;
import eu.lefler.library.repository.ReaderRepository;
import eu.lefler.library.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Configuration
public class DatabaseLoad {
    private static final Logger log = LoggerFactory.getLogger(DatabaseLoad.class);

    @Bean
    CommandLineRunner initDatabase(BookRepository bookRepo, ReaderRepository readerRepo, LoanRepository loanRepo, ReservationRepository reservationRepo) {
        return args -> {
            log.info("Preloading Book data...");
            bookRepo.save(new Book("Hobbit", "Tolkien"));
            bookRepo.save(new Book("Harry Potter", "Rowling"));
            List<Book> books = bookRepo.findAll();

            log.info("Preloading Reader data...");
            readerRepo.save(new Reader("John", "Doe", "john.doe@gmail.com"));
            readerRepo.save(new Reader("Jane", "Doe", "jane.doe@hotmail.com"));
            List<Reader> readers = readerRepo.findAll();

            log.info("Preloading Loan data...");
            loanRepo.save(new Loan(books.get(0), readers.get(0), LocalDate.now()));

            log.info("Preloading Reservation data...");
            reservationRepo.save(new Reservation(books.get(0), readers.get(1), LocalDateTime.now()));
        };
    }
}
