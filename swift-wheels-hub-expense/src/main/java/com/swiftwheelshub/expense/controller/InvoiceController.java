package com.swiftwheelshub.expense.controller;

import com.swiftwheelshub.dto.InvoiceRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.expense.service.InvoiceService;
import com.swiftwheelshub.lib.aspect.LogActivity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
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
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    @Secured("user")
    public ResponseEntity<List<InvoiceResponse>> findAllInvoices() {
        List<InvoiceResponse> allInvoiceResponses = invoiceService.findAllInvoices();

        return ResponseEntity.ok(allInvoiceResponses);
    }

    @GetMapping(path = "/active")
    @Secured("user")
    public ResponseEntity<List<InvoiceResponse>> findAllActiveInvoices() {
        List<InvoiceResponse> allInvoiceResponses = invoiceService.findAllActiveInvoices();

        return ResponseEntity.ok(allInvoiceResponses);
    }

    @GetMapping(path = "/{id}")
    @Secured("user")
    public ResponseEntity<InvoiceResponse> findInvoiceById(@PathVariable("id") Long id) {
        InvoiceResponse invoiceResponse = invoiceService.findInvoiceById(id);

        return ResponseEntity.ok(invoiceResponse);
    }

    @GetMapping(path = "/by-customer/{customerUsername}")
    @Secured("user")
    public ResponseEntity<List<InvoiceResponse>> findAllInvoicesByCustomerId(@PathVariable("customerUsername") String customerUsername) {
        List<InvoiceResponse> allInvoiceResponses = invoiceService.findAllInvoicesByCustomerUsername(customerUsername);

        return ResponseEntity.ok(allInvoiceResponses);
    }

    @GetMapping(path = "/count")
    @Secured("user")
    public ResponseEntity<Long> countInvoices() {
        Long invoices = invoiceService.countInvoices();

        return ResponseEntity.ok(invoices);
    }

    @GetMapping(path = "/active-count")
    @Secured("user")
    public ResponseEntity<Long> countActiveInvoices() {
        Long invoices = invoiceService.countAllActiveInvoices();

        return ResponseEntity.ok(invoices);
    }

    @PutMapping("/{id}")
    @Secured("user")
    @LogActivity(
            sentParameters = {"id", "invoiceRequest"},
            activityDescription = "Invoice closing"
    )
    public ResponseEntity<InvoiceResponse> closeInvoice(HttpServletRequest request,
                                                        @PathVariable("id") Long id,
                                                        @RequestBody @Validated InvoiceRequest invoiceRequest) {
        InvoiceResponse undatedinvoiceResponse = invoiceService.closeInvoice(request, id, invoiceRequest);

        return ResponseEntity.ok(undatedinvoiceResponse);
    }

}
