package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.InvoiceReprocessRequest;
import com.swiftwheelshub.expense.producer.BookingUpdateProducerService;
import com.swiftwheelshub.expense.producer.CarStatusUpdateProducerService;
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
