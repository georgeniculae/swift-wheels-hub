package com.autohub.expense.service;

import com.autohub.dto.RevenueResponse;
import com.autohub.entity.Invoice;
import com.autohub.entity.Revenue;
import com.autohub.expense.mapper.RevenueMapper;
import com.autohub.expense.repository.RevenueRepository;
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

    public void addRevenue(Invoice invoice) {
        revenueRepository.save(getRevenue(invoice));
    }

    private Revenue getRevenue(Invoice invoice) {
        Revenue revenue = new Revenue();

        revenue.setDateOfRevenue(invoice.getCarReturnDate());
        revenue.setAmountFromBooking(invoice.getTotalAmount());

        return revenue;
    }

}
