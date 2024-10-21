package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.repository.BookingRepository;
import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.dto.StatusUpdateResponse;
import com.swiftwheelshub.dto.UpdateCarRequest;
import com.swiftwheelshub.dto.UserInfo;
import com.swiftwheelshub.entity.Booking;
import com.swiftwheelshub.entity.BookingProcessStatus;
import com.swiftwheelshub.entity.BookingStatus;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshub.lib.util.AuthenticationUtil;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
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
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService implements RetryListener {

    private final BookingRepository bookingRepository;
    private final CarService carService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;
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

    public Long countUsersWithBookings() {
        return bookingRepository.countUsersWithBookings();
    }

    public BookingResponse findBookingByDateOfBooking(String searchString) {
        Booking booking = bookingRepository.findByDateOfBooking(LocalDate.parse(searchString))
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Booking from date: " + searchString + " does not exist"));

        return bookingMapper.mapEntityToDto(booking);
    }

    public Long countByLoggedInUser() {
        return bookingRepository.countByCustomerUsername(HttpRequestUtil.extractUsername());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findBookingsByLoggedInUser() {
        String username = HttpRequestUtil.extractUsername();

        try (Stream<Booking> bookingStream = bookingRepository.findBookingsByUser(username)) {
            return bookingStream.map(bookingMapper::mapEntityToDto).toList();
        }
    }

    public BigDecimal getAmountSpentByLoggedInUser() {
        return bookingRepository.sumAmountSpentByLoggedInUser(HttpRequestUtil.extractUsername());
    }

    public BigDecimal getSumOfAllBookingAmount() {
        return bookingRepository.sumAllBookingsAmount();
    }

    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public BookingResponse saveBooking(BookingRequest newBookingRequest) {
        validateBookingDates(newBookingRequest);
        AuthenticationInfo authenticationInfo = AuthenticationUtil.getAuthenticationInfo();

        try {
            CarResponse carResponse = carService.findAvailableCarById(authenticationInfo, newBookingRequest.carId());
            Booking createdBooking = createNewBooking(authenticationInfo, newBookingRequest, carResponse);

            Booking savedCreatedBooking = bookingRepository.save(createdBooking);

            StatusUpdateResponse statusUpdateResponse =
                    carService.changeCarStatus(authenticationInfo, carResponse.id(), CarState.NOT_AVAILABLE);

            BookingProcessStatus bookingProcessStatus = getBookingProcessStatus(statusUpdateResponse);
            savedCreatedBooking.setBookingProcessStatus(bookingProcessStatus);

            Booking savedBooking = bookingRepository.save(savedCreatedBooking);

            return bookingMapper.mapEntityToDto(savedBooking);
        } catch (Exception e) {
            log.error("Error occurred while saving booking: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }
    }

    public BookingResponse updateBooking(Long id, BookingRequest updatedBookingRequest) {
        validateBookingDates(updatedBookingRequest);

        try {
            Booking savedUpdatedBooking = processUpdatedBooking(id, updatedBookingRequest);

            return bookingMapper.mapEntityToDto(savedUpdatedBooking);
        } catch (Exception e) {
            log.error("Error occurred while updating booking: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }
    }

    public BookingResponse closeBooking(BookingClosingDetails bookingClosingDetails) {
        try {
            AuthenticationInfo authenticationInfo = AuthenticationUtil.getAuthenticationInfo();
            EmployeeResponse employeeResponse = employeeService.findEmployeeById(authenticationInfo, bookingClosingDetails.receptionistEmployeeId());

            Booking existingBooking = findEntityById(bookingClosingDetails.bookingId());
            existingBooking.setStatus(BookingStatus.CLOSED);
            existingBooking.setReturnBranchId(employeeResponse.workingBranchId());
            existingBooking.setBookingProcessStatus(BookingProcessStatus.IN_CLOSING);
            Booking savedIntermediateBooking = bookingRepository.save(existingBooking);

            StatusUpdateResponse statusUpdateResponse =
                    changeCarStatusWhenIsReturned(authenticationInfo, savedIntermediateBooking, bookingClosingDetails);

            BookingProcessStatus bookingProcessStatus = getBookingProcessStatus(statusUpdateResponse);
            savedIntermediateBooking.setBookingProcessStatus(bookingProcessStatus);

            Booking savedClosedBooking = bookingRepository.save(savedIntermediateBooking);

            return bookingMapper.mapEntityToDto(savedClosedBooking);
        } catch (Exception e) {
            log.error("Error occurred while closing booking: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }
    }

    public void deleteBookingByCustomerUsername(String username) {
        boolean existsInProgressBookingsByCustomer = bookingRepository.existsInProgressBookingsByCustomerUsername(username);

        if (existsInProgressBookingsByCustomer) {
            throw new SwiftWheelsHubException("There are bookings in progress for this user");
        }

        try {
            bookingRepository.deleteByCustomerUsername(username);
        } catch (Exception e) {
            throw new SwiftWheelsHubException(e.getMessage());
        }
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

    private Booking findEntityById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Booking with id " + id + " does not exist"));
    }

    private Booking createNewBooking(AuthenticationInfo authenticationInfo,
                                     BookingRequest newBookingRequest,
                                     CarResponse carResponse) {
        UserInfo userInfo = customerService.getUserByUsername(authenticationInfo);

        Booking newBooking = bookingMapper.mapDtoToEntity(newBookingRequest);
        BigDecimal amount = carResponse.amount();

        newBooking.setCustomerUsername(userInfo.username());
        newBooking.setCustomerEmail(userInfo.email());
        newBooking.setCarId(carResponse.id());
        newBooking.setDateOfBooking(LocalDate.now());
        newBooking.setRentalBranchId(carResponse.actualBranchId());
        newBooking.setStatus(BookingStatus.IN_PROGRESS);
        newBooking.setAmount(getAmount(newBookingRequest, amount));
        newBooking.setRentalCarPrice(carResponse.amount());
        newBooking.setBookingProcessStatus(BookingProcessStatus.IN_CREATION);

        return newBooking;
    }

    private BookingProcessStatus getBookingProcessStatus(StatusUpdateResponse statusUpdateResponse) {
        if (statusUpdateResponse.isUpdateSuccessful()) {
            return BookingProcessStatus.SAVED_CREATED_BOOKING;
        }

        return BookingProcessStatus.FAILED_CREATED_BOOKING;
    }

    private Booking processUpdatedBooking(Long id, BookingRequest updatedBookingRequest) {
        Booking existingBooking = findEntityById(id);

        final long existingCarId = existingBooking.getCarId();
        existingBooking.setAmount(getAmount(updatedBookingRequest, existingBooking.getRentalCarPrice()));
        existingBooking.setDateFrom(updatedBookingRequest.dateFrom());
        existingBooking.setDateTo(updatedBookingRequest.dateTo());
        existingBooking.setBookingProcessStatus(BookingProcessStatus.SAVED_UPDATED_BOOKING);

        Optional<Booking> bookingWithChangedCar =
                processBookingWhenCarIsChanged(updatedBookingRequest, existingCarId, existingBooking);

        Booking processedBooking = bookingWithChangedCar.orElse(existingBooking);

        return bookingRepository.save(processedBooking);
    }

    private Optional<Booking> processBookingWhenCarIsChanged(BookingRequest updatedBookingRequest,
                                                             long existingCarId,
                                                             Booking existingBooking) {
        long newCarId = updatedBookingRequest.carId();

        if (existingCarId == newCarId) {
            return Optional.empty();
        }

        AuthenticationInfo authenticationInfo = AuthenticationUtil.getAuthenticationInfo();
        CarResponse newCarResponse = carService.findAvailableCarById(authenticationInfo, newCarId);

        existingBooking.setAmount(getAmount(updatedBookingRequest, newCarResponse.amount()));
        existingBooking.setCarId(newCarResponse.id());
        existingBooking.setRentalBranchId(newCarResponse.actualBranchId());
        existingBooking.setBookingProcessStatus(BookingProcessStatus.IN_UPDATE);
        Booking savedIntermediateBooking = bookingRepository.save(existingBooking);

        StatusUpdateResponse statusUpdateResponse =
                updateCarsStatuses(authenticationInfo, existingCarId, updatedBookingRequest.carId());

        BookingProcessStatus bookingProcessStatus = getBookingProcessStatus(statusUpdateResponse);
        savedIntermediateBooking.setBookingProcessStatus(bookingProcessStatus);

        return Optional.of(savedIntermediateBooking);
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

    private StatusUpdateResponse updateCarsStatuses(AuthenticationInfo authenticationInfo,
                                                    Long existingCarId,
                                                    Long newCarId) {
        List<UpdateCarRequest> carsForUpdate = List.of(
                new UpdateCarRequest(existingCarId, CarState.AVAILABLE),
                new UpdateCarRequest(newCarId, CarState.NOT_AVAILABLE)
        );

        return carService.updateCarsStatuses(authenticationInfo, carsForUpdate);
    }

    private StatusUpdateResponse changeCarStatusWhenIsReturned(AuthenticationInfo authenticationInfo,
                                                               Booking savedIntermediateBooking,
                                                               BookingClosingDetails bookingClosingDetails) {
        CarUpdateDetails carUpdateDetails = new CarUpdateDetails(
                savedIntermediateBooking.getCarId(),
                bookingClosingDetails.carState(),
                bookingClosingDetails.receptionistEmployeeId()
        );

        return carService.updateCarWhenBookingIsFinished(authenticationInfo, carUpdateDetails);
    }

}
