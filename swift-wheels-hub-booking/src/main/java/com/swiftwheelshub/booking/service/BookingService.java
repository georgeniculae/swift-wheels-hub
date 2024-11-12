package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.repository.BookingRepository;
import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.BookingRollbackResponse;
import com.swiftwheelshub.dto.BookingUpdateResponse;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.StatusUpdateResponse;
import com.swiftwheelshub.dto.UpdateCarRequest;
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
import org.springframework.data.redis.core.RedisTemplate;
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

    private static final String LOCKED = "Locked";
    private final BookingRepository bookingRepository;
    private final CarService carService;
    private final CarStatusUpdaterService carStatusUpdaterService;
    private final RedisTemplate<String, String> redisTemplate;
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
        try {
            validateBookingDates(newBookingRequest);
            AuthenticationInfo authenticationInfo = AuthenticationUtil.getAuthenticationInfo();
            lockCar(newBookingRequest.carId().toString());

            CarResponse carResponse = carService.findAvailableCarById(authenticationInfo, newBookingRequest.carId());
            Booking createdBooking = createNewBooking(authenticationInfo.username(), newBookingRequest, carResponse);

            Booking savedCreatedBooking = bookingRepository.save(createdBooking);

            StatusUpdateResponse statusUpdateResponse =
                    carStatusUpdaterService.changeCarStatus(authenticationInfo, carResponse.id(), CarState.NOT_AVAILABLE);
            BookingProcessStatus bookingProcessStatus = getCreatedBookingProcessStatus(statusUpdateResponse);
            savedCreatedBooking.setBookingProcessStatus(bookingProcessStatus);
            unlockCar(savedCreatedBooking.getActualCarId().toString());

            Booking savedBooking = bookingRepository.save(savedCreatedBooking);

            return bookingMapper.mapEntityToDto(savedBooking);
        } catch (Exception e) {
            log.error("Error occurred while saving booking: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }
    }

    public BookingResponse updateBooking(Long id, BookingRequest updatedBookingRequest) {
        try {
            validateBookingDates(updatedBookingRequest);
            Booking savedUpdatedBooking = processUpdatedBooking(id, updatedBookingRequest);

            return bookingMapper.mapEntityToDto(savedUpdatedBooking);
        } catch (Exception e) {
            log.error("Error occurred while updating booking: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }
    }

    public BookingUpdateResponse closeBooking(BookingClosingDetails bookingClosingDetails) {
        try {
            Booking existingBooking = findEntityById(bookingClosingDetails.bookingId());
            existingBooking.setStatus(BookingStatus.CLOSED);
            existingBooking.setReturnBranchId(bookingClosingDetails.returnBranchId());
            existingBooking.setBookingProcessStatus(BookingProcessStatus.SAVED_CLOSED_BOOKING);
            bookingRepository.save(existingBooking);

            return new BookingUpdateResponse(true);
        } catch (Exception e) {
            log.error("Error occurred while closing booking: {}", e.getMessage());

            return new BookingUpdateResponse(false);
        }
    }

    public BookingRollbackResponse rollbackBooking(Long bookingId) {
        try {
            bookingRepository.updateStatusById(BookingStatus.IN_PROGRESS, bookingId);

            return new BookingRollbackResponse(true, bookingId);
        } catch (Exception e) {
            log.error("Error occurred while rollback: {}", e.getMessage());

            return new BookingRollbackResponse(false, bookingId);
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

    private Booking createNewBooking(String username,
                                     BookingRequest newBookingRequest,
                                     CarResponse carResponse) {
        Booking newBooking = bookingMapper.mapDtoToEntity(newBookingRequest);
        BigDecimal amount = carResponse.amount();

        newBooking.setCustomerUsername(username);
        newBooking.setActualCarId(carResponse.id());
        newBooking.setDateOfBooking(LocalDate.now());
        newBooking.setRentalBranchId(carResponse.actualBranchId());
        newBooking.setStatus(BookingStatus.IN_PROGRESS);
        newBooking.setAmount(getAmount(newBookingRequest, amount));
        newBooking.setRentalCarPrice(carResponse.amount());
        newBooking.setBookingProcessStatus(BookingProcessStatus.IN_CREATION);

        return newBooking;
    }

    private BookingProcessStatus getCreatedBookingProcessStatus(StatusUpdateResponse statusUpdateResponse) {
        if (statusUpdateResponse.isUpdateSuccessful()) {
            return BookingProcessStatus.SAVED_CREATED_BOOKING;
        }

        return BookingProcessStatus.FAILED_CREATED_BOOKING;
    }

    private BookingProcessStatus getUpdatedBookingProcessStatus(StatusUpdateResponse statusUpdateResponse) {
        if (statusUpdateResponse.isUpdateSuccessful()) {
            return BookingProcessStatus.SAVED_UPDATED_BOOKING;
        }

        return BookingProcessStatus.FAILED_UPDATED_BOOKING;
    }

    private Booking processUpdatedBooking(Long id, BookingRequest updatedBookingRequest) {
        Booking existingBooking = findEntityById(id);

        final long existingCarId = existingBooking.getActualCarId();
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

        lockCar(updatedBookingRequest.carId().toString());
        AuthenticationInfo authenticationInfo = AuthenticationUtil.getAuthenticationInfo();
        CarResponse newCarResponse = carService.findAvailableCarById(authenticationInfo, newCarId);

        existingBooking.setAmount(getAmount(updatedBookingRequest, newCarResponse.amount()));
        existingBooking.setActualCarId(newCarResponse.id());
        existingBooking.setPreviousCarId(existingCarId);
        existingBooking.setRentalBranchId(newCarResponse.actualBranchId());
        existingBooking.setBookingProcessStatus(BookingProcessStatus.IN_UPDATE);
        Booking savedIntermediateBooking = bookingRepository.save(existingBooking);

        StatusUpdateResponse statusUpdateResponse =
                updateCarsStatuses(authenticationInfo, existingCarId, updatedBookingRequest.carId());

        BookingProcessStatus bookingProcessStatus = getUpdatedBookingProcessStatus(statusUpdateResponse);
        savedIntermediateBooking.setBookingProcessStatus(bookingProcessStatus);
        unlockCar(savedIntermediateBooking.getActualCarId().toString());

        return Optional.of(savedIntermediateBooking);
    }

    private void lockCar(String carId) {
        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(carId, LOCKED);

        if (Boolean.TRUE.equals(isLocked)) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Car is unavailable");
        }
    }

    private void unlockCar(String carId) {
        redisTemplate.delete(carId);
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

        return carStatusUpdaterService.updateCarsStatuses(authenticationInfo, carsForUpdate);
    }

}
