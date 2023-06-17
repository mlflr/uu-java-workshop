package eu.lefler.library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Invoice {
    private @Id @GeneratedValue Long id;

    private double amount;
    private String description;
    private LocalDate issueDate;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        OPEN, DUE, PAID;
    }

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private Reader reader;

    public Invoice() {}
    public Invoice(double amount, String description, LocalDate dueDate, Reader reader) {
        this.amount = amount;
        this.description = description;
        this.issueDate = LocalDate.now();
        this.dueDate = dueDate;
        this.status = Status.OPEN;
        this.reader = reader;
    }

    public Long getId() { return this.id; }
    public double getAmount() { return this.amount; }
    public String getDescription() { return this.description; }
    public LocalDate getDueDate() { return this.dueDate; }
    public Status getStatus() { return this.status; }
    public Reader getReader() { return this.reader; }

    public void setId(Long id) { this.id = id; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setStatus(Status status) { this.status = status; }
    public void setReader(Reader reader) { this.reader = reader; }
}
