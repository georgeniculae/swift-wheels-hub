package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.RevenueResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.entity.Revenue;
import com.swiftwheelshub.expense.mapper.RevenueMapper;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import com.swiftwheelshub.expense.repository.RevenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final RevenueRepository revenueRepository;
    private final InvoiceRepository invoiceRepository;
    private final RevenueMapper revenueMapper;

    public BigDecimal getTotalAmount() {
        return revenueRepository.getTotalAmount();
    }

    @Transactional(readOnly = true)
    public List<RevenueResponse> findAllRevenues() {
        try (Stream<Revenue> revenueStream = revenueRepository.findAllRevenues()) {
            return revenueStream.map(revenueMapper::mapEntityToDto).toList();
        }
    }

    @Transactional(readOnly = true)
    public List<RevenueResponse> findRevenuesByDate(LocalDate dateOfRevenue) {
        try (Stream<Revenue> revenueStream = revenueRepository.findByDateOfRevenue(dateOfRevenue)) {
            return revenueStream.map(revenueMapper::mapEntityToDto).toList();
        }
    }

    @Transactional
    public void saveInvoiceAndRevenue(Invoice invoice) {
        invoiceRepository.save(invoice);
        revenueRepository.save(getRevenue(invoice));
    }

    private Revenue getRevenue(Invoice invoice) {
        Revenue revenue = new Revenue();

        revenue.setDateOfRevenue(invoice.getCarDateOfReturn());
        revenue.setAmountFromBooking(invoice.getTotalAmount());

        return revenue;
    }

}
