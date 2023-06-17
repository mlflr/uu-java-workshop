package eu.lefler.library.controller;

import eu.lefler.library.entity.Invoice;
import eu.lefler.library.repository.InvoiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceRepository repo;

    InvoiceController(InvoiceRepository repo) {
        this.repo = repo;
    }

    @GetMapping("")
    List<Invoice> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    Invoice getInvoice(@PathVariable Long id) {
        Invoice response = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        return response;
    }

    @GetMapping("/reader/{id}")
    List<Invoice> getInvoicesByReaderId(@PathVariable Long id) {
        List<Invoice> response = repo.findByReaderId(id);
        return response;
    }

    @PostMapping("")
    Invoice createInvoice(@RequestBody Invoice newInvoice) {
        return repo.save(newInvoice);
    }

    @PutMapping("/status/paid/{id}")
    Invoice updateInvoiceStatusPaid(@PathVariable Long id) {
        Invoice updatedInvoice = repo.findById(id)
                .map(invoice -> {
                    invoice.setStatus(Invoice.Status.PAID);
                    return repo.save(invoice);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        return updatedInvoice;
    }



}
