package com.carrental.expense.controller;

import com.carrental.dto.InvoiceDto;
import com.carrental.expense.service.InvoiceService;
import com.carrental.lib.aspect.LogActivity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/invoices")
@CrossOrigin(origins = "${cross-origin}")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<InvoiceDto>> findAllInvoices() {
        List<InvoiceDto> allInvoices = invoiceService.findAllInvoices();

        return ResponseEntity.ok(allInvoices);
    }

    @GetMapping(path = "/active")
    public ResponseEntity<List<InvoiceDto>> findAllActiveInvoices() {
        List<InvoiceDto> allInvoices = invoiceService.findAllActiveInvoices();

        return ResponseEntity.ok(allInvoices);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<InvoiceDto> findInvoiceById(@PathVariable("id") Long id) {
        InvoiceDto invoice = invoiceService.findInvoiceById(id);

        return ResponseEntity.ok(invoice);
    }

    @GetMapping(path = "/by-customer/{customerUsername}")
    public ResponseEntity<List<InvoiceDto>> findAllInvoicesByCustomerId(@PathVariable("customerUsername") String customerUsername) {
        List<InvoiceDto> allInvoices = invoiceService.findAllInvoicesByCustomerId(customerUsername);

        return ResponseEntity.ok(allInvoices);
    }

    @PutMapping("/{id}")
    @LogActivity(
            sentParameters = {"id", "invoiceDto"},
            activityDescription = "Invoice closing"
    )
    public ResponseEntity<InvoiceDto> closeInvoice(HttpServletRequest request,
                                                   @PathVariable("id") Long id,
                                                   @RequestBody @Valid InvoiceDto invoiceDto) {
        InvoiceDto undatedinvoiceDto = invoiceService.closeInvoice(request, id, invoiceDto);

        return ResponseEntity.ok(undatedinvoiceDto);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<Long> countInvoices() {
        Long invoices = invoiceService.countInvoices();

        return ResponseEntity.ok(invoices);
    }

    @GetMapping(path = "/active-count")
    public ResponseEntity<Long> countActiveInvoices() {
        Long invoices = invoiceService.countAllActiveInvoices();

        return ResponseEntity.ok(invoices);
    }

}
