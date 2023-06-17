package eu.lefler.library.repository;

import eu.lefler.library.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByReaderId(Long readerId);
    List<Loan> findByReaderIdAndReturnDateIsNull(Long readerId);

    List<Loan> findByBookId(Long bookId);
    List<Loan> findByBookIdAndReturnDateIsNull(Long bookId);

    List<Loan> findByReturnDateIsNull();
    List<Loan> findByReturnDateIsNullAndDueDateBefore(LocalDate date);
}
