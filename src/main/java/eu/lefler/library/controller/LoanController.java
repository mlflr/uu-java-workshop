package eu.lefler.library.controller;

import eu.lefler.library.entity.*;
import eu.lefler.library.repository.ReservationRepository;
import eu.lefler.library.request.LoanRequest;
import eu.lefler.library.repository.BookRepository;
import eu.lefler.library.repository.LoanRepository;
import eu.lefler.library.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.DAYS;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanRepository repo;
    private final BookRepository bookRepo;
    private final ReaderRepository readerRepo;
    private final ReservationRepository reservationRepo;
    private final InvoiceController invoiceController;

    @Value("${library.fees.late_return.per_day}")
    private double lateReturnFeePerDay;

    LoanController(LoanRepository repo, BookRepository bookRepo, ReaderRepository readerRepo, ReservationRepository reservationRepo, InvoiceController invoiceController) {
        this.repo = repo;
        this.bookRepo = bookRepo;
        this.readerRepo = readerRepo;
        this.reservationRepo = reservationRepo;
        this.invoiceController = invoiceController;
    }

    @GetMapping("")
    List<Loan> getAll(@RequestParam(required = false, defaultValue = "false") Boolean active) {
        return active ? repo.findByReturnDateIsNull() : repo.findAll();
    }

    @GetMapping("/{id}")
    Loan getLoan(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
    }

    @GetMapping("/reader/{id}")
    List<Loan> getLoansByReader(@PathVariable Long id, @RequestParam(required = false, defaultValue = "false") Boolean active) {
        return active ? repo.findByReaderIdAndReturnDateIsNull(id) : repo.findByReaderId(id);
    }

    @GetMapping("/book/{id}")
    List<Loan> getLoansByBook(@PathVariable Long id, @RequestParam(required = false, defaultValue = "false") Boolean active) {
        return active ? repo.findByBookIdAndReturnDateIsNull(id) : repo.findByBookId(id);
    }

    @GetMapping("/overdue")
    List<Loan> getOverdueLoans() {
        return repo.findByReturnDateIsNullAndDueDateBefore(LocalDate.now());
    }

    @PostMapping("")
    Loan createLoan(@RequestBody LoanRequest request) {
        Reader reader = readerRepo.findById(request.getReaderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reader not found"));

        // check if the reader has active subscription
        if (reader.getSubscriptionExpirationDate() == null || reader.getSubscriptionExpirationDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reader's subscription has expired");
        }

        // Check if the book is not loaned already
        List<Loan> loans = repo.findByBookIdAndReturnDateIsNull(request.getBookId());
        if (loans.size() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is already loaned");
        }

        // Check if the book is not reserved
        List<Reservation> reservations = reservationRepo.findByBookIdOrderByReservationDateAsc(request.getBookId());

        // If the book is reserved by another reader, throw an error, if it's reserved by the same reader, delete the reservation
        if (!reservations.isEmpty()) {
            Reservation res = reservations.get(0);
            if (res != null && !Objects.equals(res.getReader().getId(), request.getReaderId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is reserved by another reader");
            } else if (res != null) {
                reservationRepo.delete(res);
            }
        }

        Book book = bookRepo.findById(request.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found"));


        Loan newLoan = new Loan(book, reader, LocalDate.now());

        return repo.save(newLoan);
    }

    @PostMapping("/return/{loanId}")
    Loan returnLoan(@PathVariable Long loanId) {
        Loan loan = repo.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        loan.setReturnDate(LocalDateTime.now());

        // check if the book was returned past due date
        if (loan.getDueDate().isBefore(LocalDate.now())) {
            // create invoice for a fine
            long daysBetween = DAYS.between(loan.getDueDate(), LocalDate.now());
            Invoice invoice = new Invoice(lateReturnFeePerDay * daysBetween,
                    "Fee for late return of loan " + loanId + " (Book " + loan.getBook().getName() + ").",
                    LocalDate.now().plusMonths(1),
                    loan.getReader());
            invoiceController.createInvoice(invoice);
        }

        return repo.save(loan);
    }

    @PostMapping("/return/book/{bookId}")
    Loan returnLoanByBook(@PathVariable Long bookId) {
        List<Loan> loans = repo.findByBookIdAndReturnDateIsNull(bookId);

        if (loans.size() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is not loaned");
        } else if (loans.size() > 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is loaned multiple times");
        }

        return returnLoan(loans.get(0).getId());
    }

    @PostMapping("/extend/{loanId}")
    Loan extendLoan(@PathVariable Long loanId) {
        Loan loan = repo.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        List<Reservation> reservations = reservationRepo.findByBookId(loan.getBook().getId());

        if (!reservations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is reserved");
        }

        if(loan.getExtensionCount() >= 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan has been extended 2 times already");
        }

        loan.setDueDate(loan.getDueDate().plusDays(30));
        loan.setExtensionCount(loan.getExtensionCount() + 1);

        return repo.save(loan);
    }

}
