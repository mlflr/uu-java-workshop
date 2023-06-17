package eu.lefler.library.task;

import eu.lefler.library.controller.InvoiceController;
import eu.lefler.library.entity.Invoice;
import eu.lefler.library.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class InvoiceChecker {
    private static final Logger log = LoggerFactory.getLogger(InvoiceChecker.class);
    private final InvoiceRepository repository;

    public InvoiceChecker(InvoiceRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 30000)
    public void checkInvoices() {
        log.info("Checking invoices");
        repository.findByStatusAndDueDateBefore(Invoice.Status.OPEN, LocalDate.now())
                .forEach(invoice -> {
                    log.info("Invoice id {} is due", invoice.getId());
                    invoice.setStatus(Invoice.Status.DUE);
                    repository.save(invoice);
                });
    }
}
