package eu.lefler.library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Reader {
    private @Id @GeneratedValue Long id;
    private String name;
    private String surname;
    private String email;

    private LocalDateTime registrationDate;
    private LocalDateTime subscriptionExpirationDate;

    public Reader() {}
    public Reader(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.registrationDate = LocalDateTime.now();
    }

    public Long getId() { return this.id; }
    public String getName() { return this.name; }
    public String getSurname() { return this.surname; }
    public String getEmail() { return this.email; }
    public LocalDateTime getRegistrationDate() { return this.registrationDate; }
    public LocalDateTime getSubscriptionExpirationDate() { return this.subscriptionExpirationDate; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setEmail(String email) { this.email = email; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
    public void setSubscriptionExpirationDate(LocalDateTime subscriptionExpirationDate) { this.subscriptionExpirationDate = subscriptionExpirationDate; }
}
