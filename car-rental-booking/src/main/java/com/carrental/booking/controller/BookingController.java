package com.carrental.booking.controller;

import com.carrental.booking.service.BookingService;
import com.carrental.dto.BookingClosingDetailsDto;
import com.carrental.dto.BookingDto;
import com.carrental.lib.aspect.LogActivity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<BookingDto>> findAllBookings() {
        List<BookingDto> bookingDtoList = bookingService.findAllBookings();

        return ResponseEntity.ok(bookingDtoList);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<BookingDto> findBookingById(@PathVariable("id") Long id) {
        BookingDto bookingDto = bookingService.findBookingById(id);

        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<Long> countBookings() {
        Long numberOfBookings = bookingService.countBookings();

        return ResponseEntity.ok(numberOfBookings);
    }

    @GetMapping(path = "/count-by-current-user")
    public ResponseEntity<Long> countByLoggedInUser(HttpServletRequest request) {
        Long numberOfBookings = bookingService.countByLoggedInUser(request);

        return ResponseEntity.ok(numberOfBookings);
    }

    @GetMapping(path = "/current-date")
    public ResponseEntity<LocalDate> getCurrentDate() {
        LocalDate currentDate = bookingService.getCurrentDate();

        return ResponseEntity.ok(currentDate);
    }

    @PostMapping("/new")
    @LogActivity(
            sentParameters = "bookingDto",
            activityDescription = "Booking add"
    )
    public ResponseEntity<BookingDto> addBooking(HttpServletRequest request, @RequestBody @Valid BookingDto bookingDto) {
        BookingDto saveBookingDto = bookingService.saveBooking(request, bookingDto);

        return ResponseEntity.ok(saveBookingDto);
    }

    @PostMapping(path = "/close-booking")
    public ResponseEntity<BookingDto> closeBooking(HttpServletRequest request,
                                                   @RequestBody @Valid BookingClosingDetailsDto bookingUpdateDetailsDto) {
        BookingDto updatedBookingDto = bookingService.closeBooking(request, bookingUpdateDetailsDto);

        return ResponseEntity.ok(updatedBookingDto);
    }

    @PutMapping(path = "/{id}")
    @LogActivity(
            sentParameters = "bookingDto",
            activityDescription = "Booking update"
    )
    public ResponseEntity<BookingDto> updateBooking(HttpServletRequest request,
                                                    @PathVariable("id") Long id,
                                                    @RequestBody @Valid BookingDto bookingDto) {
        BookingDto updatedBookingDto = bookingService.updateBooking(request, id, bookingDto);

        return ResponseEntity.ok(updatedBookingDto);
    }

    @DeleteMapping(path = "/{id}")
    @LogActivity(
            sentParameters = "id",
            activityDescription = "Booking deletion"
    )
    public ResponseEntity<Void> deleteBookingById(HttpServletRequest request, @PathVariable("id") Long id) {
        bookingService.deleteBookingById(request, id);

        return ResponseEntity.noContent().build();
    }

}
