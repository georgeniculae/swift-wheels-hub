package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.InvoiceReprocessRequest;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import com.swiftwheelshub.expense.producer.BookingRollbackProducerService;
import com.swiftwheelshub.expense.producer.BookingUpdateProducerService;
import com.swiftwheelshub.expense.producer.CarStatusUpdateProducerService;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceReprocessingService implements RetryListener {

    private final BookingUpdateProducerService bookingUpdateProducerService;
    private final CarStatusUpdateProducerService carStatusUpdateProducerService;
    private final BookingRollbackProducerService bookingRollbackProducerService;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public void reprocessInvoice(InvoiceReprocessRequest invoiceReprocessRequest) {
        BookingClosingDetails bookingClosingDetails =
                getBookingClosingDetails(invoiceReprocessRequest.bookingId(), invoiceReprocessRequest.returnBranchId());

        if (bookingUpdateProducerService.closeBooking(bookingClosingDetails)) {
            if (carStatusUpdateProducerService.markCarAsAvailable(getCarUpdateDetails(invoiceReprocessRequest))) {

                invoiceRepository.findById(invoiceReprocessRequest.invoiceId())
                        .map(invoiceMapper::getSuccessfulCreatedInvoice)
                        .ifPresent(invoiceRepository::save);

                return;
            }
        }

        bookingRollbackProducerService.rollbackBooking(invoiceReprocessRequest.bookingId());

        throw new SwiftWheelsHubException("Invoice reprocessing failed");
    }

    private BookingClosingDetails getBookingClosingDetails(Long bookingId, Long returnBranchId) {
        return BookingClosingDetails.builder()
                .bookingId(bookingId)
                .returnBranchId(returnBranchId)
                .build();
    }

    private CarUpdateDetails getCarUpdateDetails(InvoiceReprocessRequest invoiceReprocessRequest) {
        return CarUpdateDetails.builder()
                .carId(invoiceReprocessRequest.carId())
                .carState(invoiceReprocessRequest.isVehicleDamaged() ? CarState.BROKEN : CarState.AVAILABLE)
                .receptionistEmployeeId(invoiceReprocessRequest.receptionistEmployeeId())
                .build();
    }

}
