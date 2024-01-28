package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.repository.BookingRepository;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarForUpdateDetails;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.dto.UpdateCarRequest;
import com.swiftwheelshub.entity.Booking;
import com.swiftwheelshub.entity.BookingStatus;
import com.swiftwheelshub.entity.CarStatus;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CarService carService;
    private final EmployeeService employeeService;
    private final BookingMapper bookingMapper;

    public List<BookingResponse> findAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::mapEntityToDto)
                .toList();
    }

    public BookingResponse findBookingById(Long id) {
        Booking booking = findEntityById(id);

        return bookingMapper.mapEntityToDto(booking);
    }

    public void deleteBookingById(HttpServletRequest request, Long id) {
        Booking existingBooking;

        try {
            existingBooking = findEntityById(id);
            bookingRepository.deleteById(id);
        } catch (Exception e) {
            throw new SwiftWheelsHubException(e);
        }

        carService.changeCarStatus(request, existingBooking.getCarId(), CarStatus.AVAILABLE);
    }

    public Long countBookings() {
        return bookingRepository.count();
    }

    public Long countUsersWithBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(Booking::getCustomerUsername)
                .distinct()
                .count();
    }

    public BookingResponse findBookingByDateOfBooking(String searchString) {
        Booking booking = bookingRepository.findByDateOfBooking(LocalDate.parse(searchString))
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Booking from date: " + searchString + " does not exist"));

        return bookingMapper.mapEntityToDto(booking);
    }

    public Long countByLoggedInUser(HttpServletRequest request) {
        return bookingRepository.countByCustomerUsername(HttpRequestUtil.extractUsername(request));
    }

    public List<BookingResponse> findBookingsByLoggedInUser(HttpServletRequest request) {
        return bookingRepository.findBookingsByUser(HttpRequestUtil.extractUsername(request))
                .stream()
                .map(bookingMapper::mapEntityToDto)
                .toList();
    }

    public Double getAmountSpentByLoggedInUser(HttpServletRequest request) {
        return findBookingsByLoggedInUser(request)
                .stream()
                .map(BookingResponse::amount)
                .filter(Objects::nonNull)
                .reduce(0D, Double::sum);
    }

    public Double getSumOfAllBookingAmount() {
        return findAllBookings()
                .stream()
                .map(BookingResponse::amount)
                .filter(Objects::nonNull)
                .reduce(0D, Double::sum);
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
            throw new SwiftWheelsHubException(e);
        }

        carService.changeCarStatus(request, carResponse.id(), CarStatus.NOT_AVAILABLE);

        return bookingResponse;
    }

    public BookingResponse updateBooking(HttpServletRequest request, Long id, BookingRequest updatedBookingRequest) {
        validateBookingDates(updatedBookingRequest);
        Booking existingBooking = findEntityById(id);

        final Long existingCarId = existingBooking.getCarId();
        Long newCarId = updatedBookingRequest.carId();

        BookingResponse bookingResponse;
        try {
            getCarIfIsChanged(request, existingCarId, newCarId)
                    .ifPresentOrElse(carDto -> {
                                existingBooking.setCarId(carDto.id());
                                existingBooking.setRentalBranchId(carDto.actualBranchId());
                                existingBooking.setAmount(getAmount(updatedBookingRequest, carDto.amount()));
                            },
                            () -> existingBooking.setAmount(getAmount(updatedBookingRequest, existingBooking.getRentalCarPrice())));

            existingBooking.setDateFrom(updatedBookingRequest.dateFrom());
            existingBooking.setDateTo(updatedBookingRequest.dateTo());

            Booking updatedBooking = bookingRepository.saveAndFlush(existingBooking);
            bookingResponse = bookingMapper.mapEntityToDto(updatedBooking);
        } catch (Exception e) {
            throw new SwiftWheelsHubException(e);
        }

        getCarsForStatusUpdate(request, existingCarId, newCarId);

        return bookingResponse;
    }

    public BookingResponse closeBooking(HttpServletRequest request, BookingClosingDetails bookingUpdateDetailsDto) {
        BookingResponse bookingResponse;

        try {
            Booking existingBooking = findEntityById(bookingUpdateDetailsDto.bookingId());

            EmployeeResponse employeeResponse =
                    employeeService.findEmployeeById(request, bookingUpdateDetailsDto.receptionistEmployeeId());

            existingBooking.setStatus(BookingStatus.CLOSED);
            existingBooking.setReturnBranchId(employeeResponse.workingBranchId());

            Booking savedBooking = bookingRepository.saveAndFlush(existingBooking);
            bookingResponse = bookingMapper.mapEntityToDto(savedBooking);
        } catch (Exception e) {
            throw new SwiftWheelsHubException(e);
        }

        updateCarWhenBookingIsClosed(request, bookingResponse, bookingUpdateDetailsDto);

        return bookingResponse;
    }

    private void validateBookingDates(BookingRequest newBookingRequest) {
        LocalDate dateFrom = newBookingRequest.dateFrom();
        LocalDate dateTo = newBookingRequest.dateTo();
        LocalDate currentDate = LocalDate.now();

        if (Objects.requireNonNull(dateFrom).isBefore(currentDate) ||
                Objects.requireNonNull(dateTo).isBefore(currentDate)) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "A date of booking cannot be in the past");
        }

        if (dateFrom.isAfter(dateTo)) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Date from is after date to");
        }
    }

    private Optional<CarResponse> getCarIfIsChanged(HttpServletRequest request, Long existingCarId, Long newCarId) {
        if (!existingCarId.equals(newCarId)) {
            CarResponse newCarResponse = carService.findAvailableCarById(request, newCarId);

            return Optional.of(newCarResponse);
        }

        return Optional.empty();
    }

    private Double getAmount(BookingRequest bookingRequest, Double amount) {
        LocalDate dateFrom = bookingRequest.dateFrom();
        LocalDate dateTo = bookingRequest.dateTo();

        int bookingDays = Period.between(Objects.requireNonNull(dateFrom), Objects.requireNonNull(dateTo)).getDays();

        if (bookingDays == 0) {
            return amount;
        }

        return bookingDays * amount;
    }

    private Booking findEntityById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Booking with id " + id + " does not exist"));
    }

    private Booking setupNewBooking(BookingRequest newBookingRequest, CarResponse carResponse) {
        Booking newBooking = bookingMapper.mapDtoToEntity(newBookingRequest);
        double amount = carResponse.amount();

        newBooking.setDateTo(newBookingRequest.dateTo());
        newBooking.setDateFrom(newBookingRequest.dateFrom());
        newBooking.setCustomerUsername(newBookingRequest.customerUsername());
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

    private void updateCarWhenBookingIsClosed(HttpServletRequest request, BookingResponse bookingResponse,
                                              BookingClosingDetails bookingClosingDetails) {
        CarForUpdateDetails carForUpdateDetails = new CarForUpdateDetails(
                bookingResponse.carId(),
                bookingClosingDetails.carState(),
                bookingClosingDetails.receptionistEmployeeId()
        );

        carService.updateCarWhenBookingIsFinished(request, carForUpdateDetails);
    }

}
