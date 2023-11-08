package com.carrental.booking.service;

import com.carrental.booking.mapper.BookingMapper;
import com.carrental.booking.repository.BookingRepository;
import com.carrental.dto.BookingClosingDetailsDto;
import com.carrental.dto.BookingDto;
import com.carrental.dto.CarDetailsForUpdateDto;
import com.carrental.dto.CarDto;
import com.carrental.dto.CarStatusEnum;
import com.carrental.dto.EmployeeDto;
import com.carrental.entity.Booking;
import com.carrental.entity.BookingStatus;
import com.carrental.lib.exception.CarRentalException;
import com.carrental.lib.exception.CarRentalNotFoundException;
import com.carrental.lib.exception.CarRentalResponseStatusException;
import com.carrental.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    public List<BookingDto> findAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::mapEntityToDto)
                .toList();
    }

    public BookingDto findBookingById(Long id) {
        Booking booking = findEntityById(id);

        return bookingMapper.mapEntityToDto(booking);
    }

    public void deleteBookingById(HttpServletRequest request, Long id) {
        Booking existingBooking;

        try {
            existingBooking = findEntityById(id);
            bookingRepository.deleteById(id);
        } catch (Exception e) {
            throw new CarRentalException(e);
        }

        carService.changeCarStatus(request, existingBooking.getCarId(), CarStatusEnum.AVAILABLE);
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

    public BookingDto findBookingByDateOfBooking(String searchString) {
        Booking booking = bookingRepository.findByDateOfBooking(LocalDate.parse(searchString))
                .orElseThrow(() -> new CarRentalNotFoundException("Booking from date: " + searchString + " does not exist"));

        return bookingMapper.mapEntityToDto(booking);
    }

    public Long countByLoggedInUser(HttpServletRequest request) {
        return bookingRepository.countByCustomerUsername(HttpRequestUtil.extractUsername(request));
    }

    public List<BookingDto> findBookingsByLoggedInUser(HttpServletRequest request) {
        return bookingRepository.findBookingsByUser(HttpRequestUtil.extractUsername(request))
                .stream()
                .map(bookingMapper::mapEntityToDto)
                .toList();
    }

    public Double getAmountSpentByLoggedInUser(HttpServletRequest request) {
        return findBookingsByLoggedInUser(request)
                .stream()
                .map(BookingDto::getAmount)
                .filter(Objects::nonNull)
                .map(BigDecimal::doubleValue)
                .reduce(0D, Double::sum);
    }

    public Double getSumOfAllBookingAmount() {
        return findAllBookings()
                .stream()
                .map(BookingDto::getAmount)
                .filter(Objects::nonNull)
                .map(BigDecimal::doubleValue)
                .reduce(0D, Double::sum);
    }

    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public BookingDto saveBooking(HttpServletRequest request, BookingDto newBookingDto) {
        BookingDto bookingDto;
        CarDto carDto;

        try {
            validateBookingDates(newBookingDto);

            carDto = carService.findAvailableCarById(request, newBookingDto.getCarId());
            Booking newBooking = setupNewBooking(newBookingDto, carDto);

            Booking savedBooking = bookingRepository.saveAndFlush(newBooking);
            bookingDto = bookingMapper.mapEntityToDto(savedBooking);
        } catch (Exception e) {
            throw new CarRentalException(e);
        }

        carService.changeCarStatus(request, carDto.getId(), CarStatusEnum.NOT_AVAILABLE);

        return bookingDto;
    }

    public BookingDto updateBooking(HttpServletRequest request, Long id, BookingDto updatedBookingDto) {
        validateBookingDates(updatedBookingDto);
        Booking existingBooking = findEntityById(id);

        final Long existingCarId = existingBooking.getCarId();
        Long newCarId = updatedBookingDto.getCarId();

        BookingDto bookingDto;
        try {
            getCarIfIsChanged(request, existingCarId, newCarId)
                    .ifPresentOrElse(carDto -> {
                                existingBooking.setCarId(carDto.getId());
                                existingBooking.setRentalBranchId(carDto.getActualBranchId());
                                existingBooking.setAmount(getAmount(updatedBookingDto, Objects.requireNonNull(carDto.getAmount()).doubleValue()));
                            },
                            () -> existingBooking.setAmount(getAmount(updatedBookingDto, existingBooking.getRentalCarPrice())));

            existingBooking.setDateFrom(updatedBookingDto.getDateFrom());
            existingBooking.setDateTo(updatedBookingDto.getDateTo());

            Booking updatedBooking = bookingRepository.saveAndFlush(existingBooking);
            bookingDto = bookingMapper.mapEntityToDto(updatedBooking);
        } catch (Exception e) {
            throw new CarRentalException(e);
        }

        getCarsForStatusUpdate(request, existingCarId, newCarId);

        return bookingDto;
    }

    public BookingDto closeBooking(HttpServletRequest request, BookingClosingDetailsDto bookingUpdateDetailsDto) {
        BookingDto bookingDto;

        try {
            Booking existingBooking = findEntityById(bookingUpdateDetailsDto.getBookingId());
            EmployeeDto employeeDto = employeeService.findEmployeeById(request, bookingUpdateDetailsDto.getReceptionistEmployeeId());

            existingBooking.setStatus(BookingStatus.CLOSED);
            existingBooking.setReturnBranchId(employeeDto.getWorkingBranchId());

            Booking savedBooking = bookingRepository.saveAndFlush(existingBooking);
            bookingDto = bookingMapper.mapEntityToDto(savedBooking);
        } catch (Exception e) {
            throw new CarRentalException(e);
        }

        updateCarWhenBookingIsClosed(request, bookingDto, bookingUpdateDetailsDto);

        return bookingDto;
    }

    @Transactional
    public void deleteBookingsByUsername(String username) {
        bookingRepository.deleteByCustomerUsername(username);
    }

    private void validateBookingDates(BookingDto newBookingDto) {
        LocalDate dateFrom = newBookingDto.getDateFrom();
        LocalDate dateTo = newBookingDto.getDateTo();
        LocalDate currentDate = LocalDate.now();

        if (Objects.requireNonNull(dateFrom).isBefore(currentDate) ||
                Objects.requireNonNull(dateTo).isBefore(currentDate)) {
            throw new CarRentalResponseStatusException(HttpStatus.BAD_REQUEST, "A date of booking cannot be in the past");
        }

        if (dateFrom.isAfter(dateTo)) {
            throw new CarRentalResponseStatusException(HttpStatus.BAD_REQUEST, "Date from is after date to");
        }
    }

    private Optional<CarDto> getCarIfIsChanged(HttpServletRequest request, Long existingCarId, Long newCarId) {
        if (!existingCarId.equals(newCarId)) {
            CarDto newCarDto = carService.findAvailableCarById(request, newCarId);

            return Optional.of(newCarDto);
        }

        return Optional.empty();
    }

    private Double getAmount(BookingDto bookingDto, Double amount) {
        LocalDate dateFrom = bookingDto.getDateFrom();
        LocalDate dateTo = bookingDto.getDateTo();

        int bookingDays = Period.between(Objects.requireNonNull(dateFrom), Objects.requireNonNull(dateTo)).getDays();

        if (bookingDays == 0) {
            return amount;
        }

        return bookingDays * amount;
    }

    private Booking findEntityById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new CarRentalNotFoundException("Booking with id " + id + " does not exist"));
    }

    private Booking setupNewBooking(BookingDto newBookingDto, CarDto carDto) {
        Booking newBooking = bookingMapper.mapDtoToEntity(newBookingDto);
        double amount = Objects.requireNonNull(carDto.getAmount()).doubleValue();

        newBooking.setDateTo(newBookingDto.getDateTo());
        newBooking.setDateFrom(newBookingDto.getDateFrom());
        newBooking.setCustomerUsername(newBookingDto.getCustomerUsername());
        newBooking.setCarId(carDto.getId());
        newBooking.setDateOfBooking(LocalDate.now());
        newBooking.setRentalBranchId(carDto.getActualBranchId());
        newBooking.setStatus(BookingStatus.IN_PROGRESS);
        newBooking.setAmount(getAmount(newBookingDto, amount));
        newBooking.setRentalCarPrice(Objects.requireNonNull(carDto.getAmount()).doubleValue());

        return newBooking;
    }

    private void getCarsForStatusUpdate(HttpServletRequest request, Long existingCarId, Long newCarId) {
        if (!existingCarId.equals(newCarId)) {
            List<CarDetailsForUpdateDto> carDetailsForUpdateDtoList = List.of(
                    new CarDetailsForUpdateDto().carId(existingCarId).carStatus(CarStatusEnum.AVAILABLE),
                    new CarDetailsForUpdateDto().carId(newCarId).carStatus(CarStatusEnum.NOT_AVAILABLE)
            );

            carService.updateCarsStatus(request, carDetailsForUpdateDtoList);
        }
    }

    private void updateCarWhenBookingIsClosed(HttpServletRequest request, BookingDto bookingDto,
                                              BookingClosingDetailsDto bookingClosingDetailsDto) {
        CarDetailsForUpdateDto carDetailsForUpdateDto = new CarDetailsForUpdateDto()
                .carId(bookingDto.getCarId())
                .receptionistEmployeeId(bookingClosingDetailsDto.getReceptionistEmployeeId())
                .carStatus(bookingClosingDetailsDto.getCarStatus());

        carService.updateCarWhenBookingIsFinished(request, carDetailsForUpdateDto);
    }

}
