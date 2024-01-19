package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.RevenueDto;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.entity.Revenue;
import com.swiftwheelshub.expense.mapper.RevenueMapper;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import com.swiftwheelshub.expense.repository.RevenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final RevenueRepository revenueRepository;
    private final InvoiceRepository invoiceRepository;
    private final RevenueMapper revenueMapper;

    public Double getTotalAmount() {
        return revenueRepository.getTotalAmount();
    }

    public List<RevenueDto> findAllRevenues() {
        return revenueRepository.findAll()
                .stream()
                .map(revenueMapper::mapEntityToDto)
                .toList();
    }

    public List<RevenueDto> findRevenuesByDate(LocalDate dateOfRevenue) {
        return revenueRepository.findByDateOfRevenue(dateOfRevenue)
                .stream()
                .map(revenueMapper::mapEntityToDto)
                .toList();
    }

    @Transactional
    public void saveInvoiceAndRevenueTransactional(Invoice invoice) {
        invoiceRepository.saveAndFlush(invoice);
        revenueRepository.saveAndFlush(getRevenue(invoice));
    }

    private Revenue getRevenue(Invoice invoice) {
        Revenue revenue = new Revenue();

        revenue.setDateOfRevenue(invoice.getCarDateOfReturn());
        revenue.setAmountFromBooking(invoice.getTotalAmount());

        return revenue;
    }

}
