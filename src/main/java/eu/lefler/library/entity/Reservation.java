package eu.lefler.library.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Reservation {
    private @Id @GeneratedValue Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private Reader reader;

    private LocalDateTime reservationDate;

    public Reservation() {}
    public Reservation(Book book, Reader reader, LocalDateTime reservationDate) {
        this.book = book;
        this.reader = reader;
        this.reservationDate = reservationDate;
    }

    public Long getId() { return this.id; }
    public Book getBook() { return this.book; }
    public Reader getReader() { return this.reader; }
    public LocalDateTime getReservationDate() { return this.reservationDate; }

    public void setId(Long id) { this.id = id; }
    public void setBook(Book book) { this.book = book; }
    public void setReader(Reader reader) { this.reader = reader; }
    public void setReservationDate(LocalDateTime reservationDate) { this.reservationDate = reservationDate; }


}
