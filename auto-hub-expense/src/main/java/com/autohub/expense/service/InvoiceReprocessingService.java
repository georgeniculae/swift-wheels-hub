package com.autohub.expense.service;

import com.autohub.dto.BookingClosingDetails;
import com.autohub.dto.CarState;
import com.autohub.dto.CarUpdateDetails;
import com.autohub.dto.InvoiceReprocessRequest;
import com.autohub.expense.producer.BookingUpdateProducerService;
import com.autohub.expense.producer.CarStatusUpdateProducerService;
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

    public void reprocessInvoice(InvoiceReprocessRequest invoiceReprocessRequest) {
        BookingClosingDetails bookingClosingDetails =
                getBookingClosingDetails(invoiceReprocessRequest.bookingId(), invoiceReprocessRequest.returnBranchId());

        carStatusUpdateProducerService.markCarAsAvailable(getCarUpdateDetails(invoiceReprocessRequest));
        bookingUpdateProducerService.closeBooking(bookingClosingDetails);
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
