package com.carrental.booking.service;

import com.carrental.booking.mapper.BookingMapper;
import com.carrental.booking.mapper.BookingMapperImpl;
import com.carrental.booking.repository.BookingRepository;
import com.carrental.booking.util.AssertionUtils;
import com.carrental.booking.util.TestUtils;
import com.carrental.dto.BookingClosingDetailsDto;
import com.carrental.dto.BookingDto;
import com.carrental.dto.CarDto;
import com.carrental.dto.EmployeeDto;
import com.carrental.entity.Booking;
import com.carrental.entity.CarStatus;
import com.carrental.exception.CarRentalNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CarService carService;

    @Mock
    private EmployeeService employeeService;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void findBookingByIdTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto actualBookingDto = assertDoesNotThrow(() -> bookingService.findBookingById(1L));
        assertNotNull(actualBookingDto);
    }

    @Test
    void findBookingByIdTest_errorOnFindingById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        CarRentalNotFoundException carRentalNotFoundException = assertThrows(CarRentalNotFoundException.class, () -> bookingService.findBookingById(1L));

        assertNotNull(carRentalNotFoundException);
        assertThat(carRentalNotFoundException.getMessage()).contains("Booking with id 1 does not exist");
    }

    @Test
    void saveBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        MockHttpServletRequest request = new MockHttpServletRequest();

        when(carService.findAvailableCarById(any(HttpServletRequest.class), anyLong())).thenReturn(carDto);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);
        doNothing().when(carService).changeCarStatus(any(HttpServletRequest.class), anyLong(), any(CarStatus.class));

        BookingDto actualBookingDto = assertDoesNotThrow(() -> bookingService.saveBooking(request, bookingDto));

        assertNotNull(actualBookingDto);

        verify(bookingMapper, times(1)).mapEntityToDto(any(Booking.class));
    }

    @Test
    void closeBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        EmployeeDto employeeDto = TestUtils.getResourceAsJson("/data/EmployeeDto.json", EmployeeDto.class);
        BookingClosingDetailsDto bookingClosingDetailsDto =
                TestUtils.getResourceAsJson("/data/BookingClosingDetailsDto.json", BookingClosingDetailsDto.class);

        MockHttpServletRequest request = new MockHttpServletRequest();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(employeeService.findEmployeeById(any(HttpServletRequest.class), anyLong())).thenReturn(employeeDto);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);

        assertDoesNotThrow(() -> bookingService.closeBooking(request, bookingClosingDetailsDto));
    }

    @Test
    void updateBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        MockHttpServletRequest request = new MockHttpServletRequest();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);

        BookingDto updatedBookingDto = assertDoesNotThrow(() -> bookingService.updateBooking(request, 1L, bookingDto));

        assertNotNull(updatedBookingDto);
    }

    @Test
    void updateBookingTest_updatedCar_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        Booking updatedBooking = TestUtils.getResourceAsJson("/data/UpdatedBooking.json", Booking.class);
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/UpdatedBookingDto.json", BookingDto.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        MockHttpServletRequest request = new MockHttpServletRequest();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(carService.findAvailableCarById(any(HttpServletRequest.class), anyLong())).thenReturn(carDto);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(updatedBooking);

        BookingDto updatedBookingDto =
                assertDoesNotThrow(() -> bookingService.updateBooking(request, 1L, bookingDto));

        assertNotNull(updatedBookingDto);
    }

    @Test
    void calculateAllAmountSpentByUserTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-USERNAME", "user");

        when(bookingRepository.findBookingsByUser(anyString())).thenReturn(List.of(booking));

        Double amount = assertDoesNotThrow(() -> bookingService.getAmountSpentByLoggedInUser(request));
        assertEquals(500, amount);
    }

    @Test
    void getSumOfAllBookingAmountTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        Double sumOfAllBookingAmount = assertDoesNotThrow(() -> bookingService.getSumOfAllBookingAmount());
        assertEquals(500, sumOfAllBookingAmount);
    }

    @Test
    void countCustomersWithBookingsTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        assertDoesNotThrow(() -> bookingService.countUsersWithBookings());
        assertEquals(1, bookingService.countUsersWithBookings());
    }

    @Test
    void findBookingByDateOfBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findByDateOfBooking(LocalDate.of(2050, Month.FEBRUARY, 20)))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = assertDoesNotThrow(() -> bookingService.findBookingByDateOfBooking("2050-02-20"));
        AssertionUtils.assertBooking(booking, bookingDto);
    }

    @Test
    void findBookingByDateOfBookingTest_errorOnFindingByDateOfBooking() {
        when(bookingRepository.findByDateOfBooking(LocalDate.of(2050, Month.FEBRUARY, 20)))
                .thenReturn(Optional.empty());

        CarRentalNotFoundException carRentalNotFoundException =
                assertThrows(CarRentalNotFoundException.class, () -> bookingService.findBookingByDateOfBooking("2050-02-20"));

        assertNotNull(carRentalNotFoundException);
        assertEquals("Booking from date: 2050-02-20 does not exist", carRentalNotFoundException.getMessage());
    }

}
