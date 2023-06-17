package eu.lefler.library.repository;

import eu.lefler.library.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByReaderId(Long readerId);
    List<Reservation> findByBookId(Long bookId);
    List<Reservation> findByBookIdOrderByReservationDateAsc(Long bookId);

    List<Reservation> findByReaderIdAndBookId(Long readerId, Long bookId);
}
