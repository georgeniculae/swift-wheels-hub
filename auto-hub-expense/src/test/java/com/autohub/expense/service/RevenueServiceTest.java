package com.autohub.expense.service;

import com.autohub.dto.RevenueResponse;
import com.autohub.entity.Revenue;
import com.autohub.expense.mapper.RevenueMapper;
import com.autohub.expense.mapper.RevenueMapperImpl;
import com.autohub.expense.repository.RevenueRepository;
import com.autohub.expense.util.AssertionUtil;
import com.autohub.expense.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevenueServiceTest {

    @InjectMocks
    private RevenueService revenueService;

    @Mock
    private RevenueRepository revenueRepository;

    @Spy
    private RevenueMapper revenueMapper = new RevenueMapperImpl();

    @Test
    void findAllRevenuesTest_success() {
        Revenue revenue = TestUtil.getResourceAsJson("/data/Revenue.json", Revenue.class);

        when(revenueRepository.findAllRevenues()).thenReturn(Stream.of(revenue));

        List<RevenueResponse> revenueResponses = revenueService.findAllRevenues();
        AssertionUtil.assertRevenueResponse(revenue, revenueResponses.getFirst());

        verify(revenueMapper).mapEntityToDto(any(Revenue.class));
    }

    @Test
    void findRevenueByDateTest_success() {
        Revenue revenue = TestUtil.getResourceAsJson("/data/Revenue.json", Revenue.class);

        when(revenueRepository.findByDateOfRevenue(any(LocalDate.class))).thenReturn(Stream.of(revenue));

        List<RevenueResponse> revenueResponses = revenueService.findRevenuesByDate(LocalDate.parse("2099-02-20"));
        AssertionUtil.assertRevenueResponse(revenue, revenueResponses.getFirst());

        verify(revenueMapper).mapEntityToDto(any(Revenue.class));
    }

}
