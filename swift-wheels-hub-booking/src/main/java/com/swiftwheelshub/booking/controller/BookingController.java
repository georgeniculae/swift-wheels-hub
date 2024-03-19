package com.swiftwheelshub.booking.controller;

import com.swiftwheelshub.booking.service.BookingService;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.lib.aspect.LogActivity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping(path = "/list")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<List<BookingResponse>> findAllBookings() {
        List<BookingResponse> bookingResponses = bookingService.findAllBookings();

        return ResponseEntity.ok(bookingResponses);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<BookingResponse> findBookingById(@PathVariable("id") Long id) {
        BookingResponse bookingResponse = bookingService.findBookingById(id);

        return ResponseEntity.ok(bookingResponse);
    }

    @GetMapping(path = "/count")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<Long> countBookings() {
        Long numberOfBookings = bookingService.countBookings();

        return ResponseEntity.ok(numberOfBookings);
    }

    @GetMapping(path = "/count-by-current-user")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<Long> countByLoggedInUser(HttpServletRequest request) {
        Long numberOfBookings = bookingService.countByLoggedInUser(request);

        return ResponseEntity.ok(numberOfBookings);
    }

    @GetMapping(path = "/current-date")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<LocalDate> getCurrentDate() {
        LocalDate currentDate = bookingService.getCurrentDate();

        return ResponseEntity.ok(currentDate);
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('user')")
    @LogActivity(
            sentParameters = "bookingRequest",
            activityDescription = "Booking add"
    )
    public ResponseEntity<BookingResponse> addBooking(HttpServletRequest request,
                                                      @RequestBody @Validated BookingRequest bookingRequest) {
        BookingResponse saveBookingResponse = bookingService.saveBooking(request, bookingRequest);

        return ResponseEntity.ok(saveBookingResponse);
    }

    @PostMapping(path = "/close-booking")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<BookingResponse> closeBooking(HttpServletRequest request,
                                                        @RequestBody @Validated BookingClosingDetails bookingClosingDetails) {
        BookingResponse updatedBookingResponse = bookingService.closeBooking(request, bookingClosingDetails);

        return ResponseEntity.ok(updatedBookingResponse);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('user')")
    @LogActivity(
            sentParameters = "bookingRequest",
            activityDescription = "Booking update"
    )
    public ResponseEntity<BookingResponse> updateBooking(HttpServletRequest request,
                                                         @PathVariable("id") Long id,
                                                         @RequestBody @Validated BookingRequest bookingRequest) {
        BookingResponse updatedBookingResponse = bookingService.updateBooking(request, id, bookingRequest);

        return ResponseEntity.ok(updatedBookingResponse);
    }

    @DeleteMapping(path = "/{username}")
    @PreAuthorize("hasAuthority('user')")
    @LogActivity(
            sentParameters = "username",
            activityDescription = "Booking deletion"
    )
    public ResponseEntity<Void> deleteBookingById(HttpServletRequest request,
                                                  @PathVariable("username") String username) {
        bookingService.deleteBookingByCustomerUsername(request, username);

        return ResponseEntity.noContent().build();
    }

}
