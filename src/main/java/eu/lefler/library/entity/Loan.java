package eu.lefler.library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Loan {
    private @Id @GeneratedValue Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private Reader reader;

    private LocalDate beginDate;
    private LocalDate dueDate;
    private LocalDateTime returnDate;

    private int extensionCount;

    public Loan() {}

    public Loan(Book book, Reader reader, LocalDate beginDate) {
        this.book = book;
        this.reader = reader;
        this.beginDate = beginDate;
        this.dueDate = beginDate.plusDays(30);
        this.returnDate = null;
        this.extensionCount = 0;
    }

    public Long getId() { return this.id; }
    public Book getBook() { return this.book; }
    public Reader getReader() { return this.reader; }
    public LocalDate getBeginDate() { return this.beginDate; }
    public LocalDate getDueDate() { return this.dueDate; }
    public LocalDateTime getReturnDate() { return this.returnDate; }
    public int getExtensionCount() { return this.extensionCount; }

    public void setId(Long id) { this.id = id; }
    public void setBook(Book book) { this.book = book; }
    public void setReader(Reader reader) { this.reader = reader; }
    public void setBeginDat(LocalDate beginDate) { this.beginDate = beginDate; }
    public void setDueDate(LocalDate endDate) { this.dueDate = endDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
    public void setExtensionCount(int extensionCount) { this.extensionCount = extensionCount; }


}
