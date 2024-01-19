package com.swiftwheelshub.expense.controller;

import com.swiftwheelshub.dto.RevenueDto;
import com.swiftwheelshub.expense.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/revenues")
public class RevenueController {

    private final RevenueService revenueService;

    @GetMapping
    public ResponseEntity<List<RevenueDto>> findAllRevenues() {
        List<RevenueDto> revenueDtoList = revenueService.findAllRevenues();

        return ResponseEntity.ok(revenueDtoList);
    }

    @GetMapping(path = "/total")
    public ResponseEntity<Double> getTotalAmount() {
        Double totalAmount = revenueService.getTotalAmount();

        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping(path = "/{date}")
    public ResponseEntity<List<RevenueDto>> findRevenuesByDate(@PathVariable("date") LocalDate date) {
        List<RevenueDto> revenues = revenueService.findRevenuesByDate(date);

        return ResponseEntity.ok(revenues);
    }

}
