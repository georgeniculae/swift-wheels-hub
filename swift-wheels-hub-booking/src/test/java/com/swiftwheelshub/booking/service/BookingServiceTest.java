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
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.entity.Booking;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.lib.security.ApiKeyAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(carService.findAvailableCarById(anyString(), anyCollection(), anyLong())).thenReturn(carResponse);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        doNothing().when(carService).changeCarStatus(anyString(), anyCollection(), anyLong(), any(CarState.class));

        BookingResponse actualBookingResponse =
                assertDoesNotThrow(() -> bookingService.saveBooking(bookingRequest));

        assertNotNull(actualBookingResponse);

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void closeBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        EmployeeResponse employeeRequest =
                TestUtils.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        BookingClosingDetails bookingClosingDetails =
                TestUtils.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(employeeService.findEmployeeById(anyString(), anyCollection(), anyLong())).thenReturn(employeeRequest);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertDoesNotThrow(() -> bookingService.closeBooking(bookingClosingDetails));
    }

    @Test
    void updateBookingTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingRequest bookingRequest = TestUtils.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse updatedBookingResponse =
                assertDoesNotThrow(() -> bookingService.updateBooking(1L, bookingRequest));

        assertNotNull(updatedBookingResponse);
    }

    @Test
    void updateBookingTest_updatedCar_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);
        Booking updatedBooking = TestUtils.getResourceAsJson("/data/UpdatedBooking.json", Booking.class);
        BookingRequest bookingRequest = TestUtils.getResourceAsJson("/data/UpdatedBookingRequest.json", BookingRequest.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(carService.findAvailableCarById(anyString(), anyCollection(), anyLong())).thenReturn(carResponse);
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);

        BookingResponse updatedBookingResponse =
                assertDoesNotThrow(() -> bookingService.updateBooking(1L, bookingRequest));

        assertNotNull(updatedBookingResponse);
    }

    @Test
    void calculateAllAmountSpentByUserTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-USERNAME", "user");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

        when(bookingRepository.findBookingsByUser(anyString())).thenReturn(Stream.of(booking));

        BigDecimal amount = assertDoesNotThrow(() -> bookingService.getAmountSpentByLoggedInUser());
        assertEquals(BigDecimal.valueOf(500), amount);
    }

    @Test
    void getSumOfAllBookingAmountTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findAllBookings()).thenReturn(Stream.of(booking));

        BigDecimal sumOfAllBookingAmount = assertDoesNotThrow(() -> bookingService.getSumOfAllBookingAmount());
        assertEquals(BigDecimal.valueOf(500), sumOfAllBookingAmount);
    }

    @Test
    void countCustomersWithBookingsTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findAllBookings()).thenReturn(Stream.of(booking));

        Long bookings = bookingService.countUsersWithBookings();
        assertEquals(1, bookings);
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
        assertEquals("Booking from date: 2050-02-20 does not exist", swiftWheelsHubNotFoundException.getReason());
    }

    @Test
    void deleteBookingByIdTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.findByCustomerUsername(anyString())).thenReturn(List.of(booking));
        doNothing().when(bookingRepository).deleteByCustomerUsername(anyString());
        doNothing().when(carService).updateCarsStatus(anyString(), anyCollection(), anyList());

        bookingService.deleteBookingByCustomerUsername("user");
    }

}
