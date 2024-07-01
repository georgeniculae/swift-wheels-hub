package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.repository.BookingRepository;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.dto.UpdateCarRequest;
import com.swiftwheelshub.entity.Booking;
import com.swiftwheelshub.entity.BookingStatus;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService implements RetryListener {

    private final BookingRepository bookingRepository;
    private final CarService carService;
    private final EmployeeService employeeService;
    private final BookingMapper bookingMapper;

    @Transactional(readOnly = true)
    public List<BookingResponse> findAllBookings() {
        try (Stream<Booking> bookingStream = bookingRepository.findAllBookings()) {
            return bookingStream.map(bookingMapper::mapEntityToDto).toList();
        }
    }

    public BookingResponse findBookingById(Long id) {
        Booking booking = findEntityById(id);

        return bookingMapper.mapEntityToDto(booking);
    }

    public Long countBookings() {
        return bookingRepository.count();
    }

    @Transactional(readOnly = true)
    public Long countUsersWithBookings() {
        try (Stream<Booking> bookingStream = bookingRepository.findAllBookings()) {
            return bookingStream.map(Booking::getCustomerUsername)
                    .distinct()
                    .count();
        }
    }

    public BookingResponse findBookingByDateOfBooking(String searchString) {
        Booking booking = bookingRepository.findByDateOfBooking(LocalDate.parse(searchString))
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Booking from date: " + searchString + " does not exist"));

        return bookingMapper.mapEntityToDto(booking);
    }

    public Long countByLoggedInUser(HttpServletRequest request) {
        return bookingRepository.countByCustomerUsername(HttpRequestUtil.extractUsername(request));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findBookingsByLoggedInUser(HttpServletRequest request) {
        String username = HttpRequestUtil.extractUsername(request);

        try (Stream<Booking> bookingStream = bookingRepository.findBookingsByUser(username)) {
            return bookingStream.map(bookingMapper::mapEntityToDto).toList();
        }
    }

    public BigDecimal getAmountSpentByLoggedInUser(HttpServletRequest request) {
        return findBookingsByLoggedInUser(request)
                .stream()
                .map(BookingResponse::amount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSumOfAllBookingAmount() {
        return findAllBookings()
                .stream()
                .map(BookingResponse::amount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public BookingResponse saveBooking(HttpServletRequest request, BookingRequest newBookingRequest) {
        BookingResponse bookingResponse;
        CarResponse carResponse;

        try {
            validateBookingDates(newBookingRequest);

            carResponse = carService.findAvailableCarById(request, newBookingRequest.carId());
            Booking newBooking = setupNewBooking(newBookingRequest, carResponse);

            Booking savedBooking = bookingRepository.saveAndFlush(newBooking);
            bookingResponse = bookingMapper.mapEntityToDto(savedBooking);
        } catch (Exception e) {
            log.error("Error occurred while saving booking: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }

        carService.changeCarStatus(request, carResponse.id(), CarState.NOT_AVAILABLE);

        return bookingResponse;
    }

    public BookingResponse updateBooking(HttpServletRequest request, Long id, BookingRequest updatedBookingRequest) {
        validateBookingDates(updatedBookingRequest);
        Booking existingBooking = findEntityById(id);

        final Long existingCarId = existingBooking.getCarId();

        BookingResponse bookingResponse;

        try {
            Booking updatedExistingBooking =
                    updateExistingBooking(request, updatedBookingRequest, existingCarId, existingBooking);

            Booking updatedBooking = bookingRepository.saveAndFlush(updatedExistingBooking);
            bookingResponse = bookingMapper.mapEntityToDto(updatedBooking);
        } catch (Exception e) {
            log.error("Error occurred while updating booking: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }

        getCarsForStatusUpdate(request, existingCarId, updatedBookingRequest.carId());

        return bookingResponse;
    }

    public BookingResponse closeBooking(HttpServletRequest request, BookingClosingDetails bookingClosingDetails) {
        BookingResponse bookingResponse;

        try {
            Booking existingBooking = findEntityById(bookingClosingDetails.bookingId());

            EmployeeResponse employeeResponse =
                    employeeService.findEmployeeById(request, bookingClosingDetails.receptionistEmployeeId());

            existingBooking.setStatus(BookingStatus.CLOSED);
            existingBooking.setReturnBranchId(employeeResponse.workingBranchId());

            Booking savedBooking = bookingRepository.saveAndFlush(existingBooking);
            bookingResponse = bookingMapper.mapEntityToDto(savedBooking);
        } catch (Exception e) {
            log.error("Error occurred while closing booking: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }

        updateCarWhenIsReturned(request, bookingResponse, bookingClosingDetails);

        return bookingResponse;
    }

    public void deleteBookingByCustomerUsername(HttpServletRequest request, String username) {
        List<Booking> existingBookings = bookingRepository.findByCustomerUsername(username);

        try {
            bookingRepository.deleteByCustomerUsername(username);
        } catch (Exception e) {
            throw new SwiftWheelsHubException(e.getMessage());
        }

        carService.updateCarsStatus(request, getUpdateCarRequests(existingBookings));
    }

    private void validateBookingDates(BookingRequest newBookingRequest) {
        LocalDate dateFrom = newBookingRequest.dateFrom();
        LocalDate dateTo = newBookingRequest.dateTo();
        LocalDate currentDate = LocalDate.now();

        if (dateFrom.isBefore(currentDate) || dateTo.isBefore(currentDate)) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "A date of booking cannot be in the past");
        }

        if (dateFrom.isAfter(dateTo)) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Date from is after date to");
        }
    }

    private Booking updateExistingBooking(HttpServletRequest request, BookingRequest updatedBookingRequest,
                                          Long existingCarId, Booking existingBooking) {
        Long newCarId = updatedBookingRequest.carId();
        BigDecimal amount = existingBooking.getRentalCarPrice();

        if (!existingCarId.equals(newCarId)) {
            CarResponse newCarResponse = carService.findAvailableCarById(request, newCarId);

            amount = newCarResponse.amount();
            existingBooking.setCarId(newCarResponse.id());
            existingBooking.setRentalBranchId(newCarResponse.actualBranchId());
        }

        existingBooking.setAmount(getAmount(updatedBookingRequest, amount));
        existingBooking.setDateFrom(updatedBookingRequest.dateFrom());
        existingBooking.setDateTo(updatedBookingRequest.dateTo());

        return existingBooking;
    }

    private BigDecimal getAmount(BookingRequest bookingRequest, BigDecimal amount) {
        LocalDate dateFrom = bookingRequest.dateFrom();
        LocalDate dateTo = bookingRequest.dateTo();

        int bookingDays = Period.between(dateFrom, dateTo).getDays();

        if (bookingDays == 0) {
            return amount;
        }

        return amount.multiply(BigDecimal.valueOf(bookingDays));
    }

    private Booking findEntityById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Booking with id " + id + " does not exist"));
    }

    private Booking setupNewBooking(BookingRequest newBookingRequest, CarResponse carResponse) {
        Booking newBooking = bookingMapper.mapDtoToEntity(newBookingRequest);
        BigDecimal amount = carResponse.amount();

        newBooking.setCustomerUsername(newBookingRequest.customerUsername());
        newBooking.setCustomerEmail(newBooking.getCustomerEmail());
        newBooking.setCarId(carResponse.id());
        newBooking.setDateOfBooking(LocalDate.now());
        newBooking.setRentalBranchId(carResponse.actualBranchId());
        newBooking.setStatus(BookingStatus.IN_PROGRESS);
        newBooking.setAmount(getAmount(newBookingRequest, amount));
        newBooking.setRentalCarPrice(carResponse.amount());

        return newBooking;
    }

    private void getCarsForStatusUpdate(HttpServletRequest request, Long existingCarId, Long newCarId) {
        if (!existingCarId.equals(newCarId)) {
            List<UpdateCarRequest> carsForUpdate = List.of(
                    new UpdateCarRequest(existingCarId, CarState.AVAILABLE),
                    new UpdateCarRequest(newCarId, CarState.NOT_AVAILABLE)
            );

            carService.updateCarsStatus(request, carsForUpdate);
        }
    }

    private void updateCarWhenIsReturned(HttpServletRequest request, BookingResponse bookingResponse,
                                         BookingClosingDetails bookingClosingDetails) {
        CarUpdateDetails carUpdateDetails = new CarUpdateDetails(
                bookingResponse.carId(),
                bookingClosingDetails.carState(),
                bookingClosingDetails.receptionistEmployeeId()
        );

        carService.updateCarWhenBookingIsFinished(request, carUpdateDetails);
    }

    private List<UpdateCarRequest> getUpdateCarRequests(List<Booking> existingBookings) {
        return existingBookings.stream()
                .map(booking -> UpdateCarRequest.builder()
                        .carId(booking.getCarId())
                        .carState(CarState.AVAILABLE)
                        .build())
                .toList();
    }

}
