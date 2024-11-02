package com.swiftwheelshub.expense.scheduler;

import com.swiftwheelshub.expense.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailedInvoiceScheduler {

    private final InvoiceRepository invoiceRepository;

    public void processFailedInvoices() {

    }

}
