package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.mapper.BookingMapperImpl;
import com.swiftwheelshub.booking.repository.BookingRepository;
import com.swiftwheelshub.booking.util.AssertionUtils;
import com.swiftwheelshub.booking.util.TestUtil;
import com.swiftwheelshub.dto.AuthenticationInfo;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.BookingUpdateResponse;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.StatusUpdateResponse;
import com.swiftwheelshub.entity.Booking;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.lib.security.ApiKeyAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    private CarStatusUpdaterService carStatusUpdaterService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void findBookingByIdTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

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
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingRequest bookingRequest = TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulStatusUpdateResponse.json", StatusUpdateResponse.class);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString())).thenReturn(false);
        when(carService.findAvailableCarById(any(AuthenticationInfo.class), anyLong())).thenReturn(carResponse);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(carStatusUpdaterService.changeCarStatus(any(AuthenticationInfo.class), anyLong(), any(CarState.class)))
                .thenReturn(statusUpdateResponse);
        when(redisTemplate.delete(anyString())).thenReturn(true);

        BookingResponse actualBookingResponse =
                assertDoesNotThrow(() -> bookingService.saveBooking(bookingRequest));

        assertNotNull(actualBookingResponse);

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void closeBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingClosingDetails bookingClosingDetails =
                TestUtil.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");
        httpServletRequest.addHeader("X-ROLES", "ROLE_user");

        RequestAttributes servletWebRequest = new ServletWebRequest(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingUpdateResponse bookingUpdateResponse = bookingService.closeBooking(bookingClosingDetails);
        assertTrue(bookingUpdateResponse.isSuccessful());
    }

    @Test
    void updateBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingRequest bookingRequest = TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

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
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        Booking updatedBooking = TestUtil.getResourceAsJson("/data/UpdatedBooking.json", Booking.class);
        BookingRequest bookingRequest = TestUtil.getResourceAsJson("/data/UpdatedBookingRequest.json", BookingRequest.class);
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulStatusUpdateResponse.json", StatusUpdateResponse.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");
        httpServletRequest.addHeader("X-ROLES", "ROLE_user");

        RequestAttributes servletWebRequest = new ServletWebRequest(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString())).thenReturn(false);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(carService.findAvailableCarById(any(AuthenticationInfo.class), anyLong())).thenReturn(carResponse);
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);
        when(carStatusUpdaterService.updateCarsStatuses(any(AuthenticationInfo.class), anyList()))
                .thenReturn(statusUpdateResponse);
        when(redisTemplate.delete(anyString())).thenReturn(true);

        BookingResponse updatedBookingResponse =
                assertDoesNotThrow(() -> bookingService.updateBooking(1L, bookingRequest));

        assertNotNull(updatedBookingResponse);
    }

    @Test
    void calculateAllAmountSpentByUserTest_success() {
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-USERNAME", "user");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

        when(bookingRepository.sumAmountSpentByLoggedInUser(anyString())).thenReturn(BigDecimal.valueOf(500));

        BigDecimal amount = assertDoesNotThrow(() -> bookingService.getAmountSpentByLoggedInUser());
        assertEquals(BigDecimal.valueOf(500), amount);
    }

    @Test
    void getSumOfAllBookingAmountTest_success() {
        when(bookingRepository.sumAllBookingsAmount()).thenReturn(BigDecimal.valueOf(500));

        BigDecimal sumOfAllBookingAmount = assertDoesNotThrow(() -> bookingService.getSumOfAllBookingAmount());
        assertEquals(BigDecimal.valueOf(500), sumOfAllBookingAmount);
    }

    @Test
    void countCustomersWithBookingsTest_success() {
        when(bookingRepository.countUsersWithBookings()).thenReturn(1L);

        Long bookings = bookingService.countUsersWithBookings();
        assertEquals(1, bookings);
    }

    @Test
    void findBookingByDateOfBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findByDateOfBooking(LocalDate.of(2099, Month.FEBRUARY, 20)))
                .thenReturn(Optional.of(booking));

        BookingResponse bookingResponse =
                assertDoesNotThrow(() -> bookingService.findBookingByDateOfBooking("2099-02-20"));

        AssertionUtils.assertBooking(booking, bookingResponse);
    }

    @Test
    void findBookingByDateOfBookingTest_errorOnFindingByDateOfBooking() {
        when(bookingRepository.findByDateOfBooking(LocalDate.of(2099, Month.FEBRUARY, 20)))
                .thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> bookingService.findBookingByDateOfBooking("2099-02-20"));

        assertNotNull(swiftWheelsHubNotFoundException);
        assertEquals("Booking from date: 2099-02-20 does not exist", swiftWheelsHubNotFoundException.getReason());
    }

    @Test
    void deleteBookingByIdTest_success() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.existsInProgressBookingsByCustomerUsername(anyString())).thenReturn(false);
        doNothing().when(bookingRepository).deleteByCustomerUsername(anyString());

        assertDoesNotThrow(() -> bookingService.deleteBookingByCustomerUsername("user"));
    }

    @Test
    void deleteBookingByIdTest_error_bookingsInProgress() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.existsInProgressBookingsByCustomerUsername(anyString())).thenReturn(true);

        SwiftWheelsHubException swiftWheelsHubException =
                assertThrows(SwiftWheelsHubException.class, () -> bookingService.deleteBookingByCustomerUsername("user"));

        assertNotNull(swiftWheelsHubException);
    }

}
