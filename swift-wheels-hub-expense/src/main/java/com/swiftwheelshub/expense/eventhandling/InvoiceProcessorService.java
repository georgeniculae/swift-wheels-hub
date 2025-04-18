package com.swiftwheelshub.expense.eventhandling;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.InvoiceReprocessRequest;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import com.swiftwheelshub.expense.producer.BookingUpdateProducerService;
import com.swiftwheelshub.expense.producer.CarStatusUpdateProducerService;
import com.swiftwheelshub.expense.producer.FailedInvoiceDlqProducerService;
import com.swiftwheelshub.expense.service.RevenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceProcessorService {

    private final RevenueService revenueService;
    private final BookingUpdateProducerService bookingUpdateProducerService;
    private final CarStatusUpdateProducerService carStatusUpdateProducerService;
    private final InvoiceMapper invoiceMapper;
    private final FailedInvoiceDlqProducerService failedInvoiceDlqProducerService;

    public void processInvoice(Invoice existingInvoiceUpdated) {
        boolean successfulUpdate = updateCarAndBooking(existingInvoiceUpdated);

        if (successfulUpdate) {
            revenueService.processClosing(existingInvoiceUpdated);
            log.info("Invoice with id: {} has been successfully closed", existingInvoiceUpdated.getId());

            return;
        }

        processFailedInvoice(existingInvoiceUpdated);
    }

    private void processFailedInvoice(Invoice existingInvoiceUpdated) {
        InvoiceReprocessRequest invoiceReprocessRequest =
                invoiceMapper.mapToInvoiceReprocessRequest(existingInvoiceUpdated);

        failedInvoiceDlqProducerService.sendMessage(invoiceReprocessRequest);

        log.warn("Invoice with id: {} has failed to close, storing it to DLQ", existingInvoiceUpdated.getId());
    }

    private boolean updateCarAndBooking(Invoice invoice) {
        boolean isCarMarkedAsAvailable = carStatusUpdateProducerService.markCarAsAvailable(getCarUpdateDetails(invoice));

        if (isCarMarkedAsAvailable) {
            return closeBooking(invoice);
        }

        return false;
    }

    private boolean closeBooking(Invoice invoice) {
        Long bookingId = invoice.getBookingId();
        Long returnBranchId = invoice.getReturnBranchId();
        BookingClosingDetails bookingClosingDetails = getBookingClosingDetails(bookingId, returnBranchId);

        return bookingUpdateProducerService.closeBooking(bookingClosingDetails);
    }

    private CarUpdateDetails getCarUpdateDetails(Invoice invoice) {
        return CarUpdateDetails.builder()
                .carId(invoice.getCarId())
                .receptionistEmployeeId(invoice.getReceptionistEmployeeId())
                .carState(invoice.getIsVehicleDamaged() ? CarState.BROKEN : CarState.AVAILABLE)
                .build();
    }

    private BookingClosingDetails getBookingClosingDetails(Long bookingId, Long returnBranchId) {
        return BookingClosingDetails.builder()
                .bookingId(bookingId)
                .returnBranchId(returnBranchId)
                .build();
    }

}
