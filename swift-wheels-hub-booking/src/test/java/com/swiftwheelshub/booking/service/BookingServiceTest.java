package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.mapper.BookingMapperImpl;
import com.swiftwheelshub.booking.repository.BookingRepository;
import com.swiftwheelshub.booking.util.AssertionUtils;
import com.swiftwheelshub.booking.util.TestUtils;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.entity.Booking;
import com.swiftwheelshub.entity.CarStatus;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
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

        BookingResponse actualBookingResponse = assertDoesNotThrow(() -> bookingService.findBookingById(1L));
        assertNotNull(actualBookingResponse);
    }

    @Test
    void findBookingByIdTest_errorOnFindingById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> bookingService.findBookingById(1L));

        assertNotNull(swiftWheelsHubNotFoundException);
        assertThat(swiftWheelsHubNotFoundException.getMessage()).contains("Booking with id 1 does not exist");
    }

    @Test
    void saveBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingRequest bookingRequest = TestUtils.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        MockHttpServletRequest request = new MockHttpServletRequest();

        when(carService.findAvailableCarById(any(HttpServletRequest.class), anyLong())).thenReturn(carResponse);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);
        doNothing().when(carService).changeCarStatus(any(HttpServletRequest.class), anyLong(), any(CarStatus.class));

        BookingResponse actualBookingResponse =
                assertDoesNotThrow(() -> bookingService.saveBooking(request, bookingRequest));

        assertNotNull(actualBookingResponse);

        verify(bookingMapper, times(1)).mapEntityToDto(any(Booking.class));
    }

    @Test
    void closeBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        EmployeeResponse employeeRequest =
                TestUtils.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        BookingClosingDetails bookingClosingDetails =
                TestUtils.getResourceAsJson("/data/BookingClosingDetailsDto.json", BookingClosingDetails.class);

        MockHttpServletRequest request = new MockHttpServletRequest();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(employeeService.findEmployeeById(any(HttpServletRequest.class), anyLong())).thenReturn(employeeRequest);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);

        assertDoesNotThrow(() -> bookingService.closeBooking(request, bookingClosingDetails));
    }

    @Test
    void updateBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingRequest bookingRequest = TestUtils.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        MockHttpServletRequest request = new MockHttpServletRequest();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);

        BookingResponse updatedBookingResponse =
                assertDoesNotThrow(() -> bookingService.updateBooking(request, 1L, bookingRequest));

        assertNotNull(updatedBookingResponse);
    }

    @Test
    void updateBookingTest_updatedCar_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        Booking updatedBooking = TestUtils.getResourceAsJson("/data/UpdatedBooking.json", Booking.class);
        BookingRequest bookingRequest = TestUtils.getResourceAsJson("/data/UpdatedBookingDto.json", BookingRequest.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        MockHttpServletRequest request = new MockHttpServletRequest();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(carService.findAvailableCarById(any(HttpServletRequest.class), anyLong())).thenReturn(carResponse);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(updatedBooking);

        BookingResponse updatedBookingResponse =
                assertDoesNotThrow(() -> bookingService.updateBooking(request, 1L, bookingRequest));

        assertNotNull(updatedBookingResponse);
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

        BookingResponse bookingResponse =
                assertDoesNotThrow(() -> bookingService.findBookingByDateOfBooking("2050-02-20"));

        AssertionUtils.assertBooking(booking, bookingResponse);
    }

    @Test
    void findBookingByDateOfBookingTest_errorOnFindingByDateOfBooking() {
        when(bookingRepository.findByDateOfBooking(LocalDate.of(2050, Month.FEBRUARY, 20)))
                .thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> bookingService.findBookingByDateOfBooking("2050-02-20"));

        assertNotNull(swiftWheelsHubNotFoundException);
        assertEquals("Booking from date: 2050-02-20 does not exist", swiftWheelsHubNotFoundException.getMessage());
    }

}
