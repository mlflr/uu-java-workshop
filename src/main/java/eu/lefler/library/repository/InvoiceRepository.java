package eu.lefler.library.repository;

import eu.lefler.library.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByReaderId(Long readerId);
}
