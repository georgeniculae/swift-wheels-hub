package com.carrental.expense.service;

import com.carrental.dto.RevenueDto;
import com.carrental.entity.Invoice;
import com.carrental.entity.Revenue;
import com.carrental.expense.mapper.RevenueMapper;
import com.carrental.expense.repository.InvoiceRepository;
import com.carrental.expense.repository.RevenueRepository;
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
