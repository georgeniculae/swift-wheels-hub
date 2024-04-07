package com.swiftwheelshub.expense.controller;

import com.swiftwheelshub.dto.RevenueResponse;
import com.swiftwheelshub.expense.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/revenues")
public class RevenueController {

    private final RevenueService revenueService;

    @GetMapping
    @Secured("admin")
    public ResponseEntity<List<RevenueResponse>> findAllRevenues() {
        List<RevenueResponse> revenueResponses = revenueService.findAllRevenues();

        return ResponseEntity.ok(revenueResponses);
    }

    @GetMapping(path = "/total")
    @Secured("admin")
    public ResponseEntity<BigDecimal> getTotalAmount() {
        BigDecimal totalAmount = revenueService.getTotalAmount();

        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping(path = "/{date}")
    @Secured("admin")
    public ResponseEntity<List<RevenueResponse>> findRevenuesByDate(@PathVariable("date") LocalDate date) {
        List<RevenueResponse> revenueResponses = revenueService.findRevenuesByDate(date);

        return ResponseEntity.ok(revenueResponses);
    }

}
