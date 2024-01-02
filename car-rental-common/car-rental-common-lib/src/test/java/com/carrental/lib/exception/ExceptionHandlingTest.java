package com.carrental.lib.exception;

import com.carrental.exception.CarRentalException;
import com.carrental.exception.CarRentalNotFoundException;
import com.carrental.exception.CarRentalResponseStatusException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlingTest {

    private final ExceptionHandling exceptionHandling = new ExceptionHandling();

    @Test
    void handleExceptionTest() {
        Exception exception = new Exception("Exception");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Map<String, Object>> responseEntity = exceptionHandling.handleException(exception, webRequest);

        assertEquals(500, responseEntity.getStatusCode().value());
    }

    @Test
    void handleNotFoundExceptionTest() {
        CarRentalNotFoundException carRentalNotFoundException = new CarRentalNotFoundException("Resource not found");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Map<String, Object>> responseEntity =
                exceptionHandling.handleNotFoundException(carRentalNotFoundException, webRequest);

        assertEquals(404, responseEntity.getStatusCode().value());
    }

    @Test
    void handleCarRentalResponseStatusExceptionTest() {
        CarRentalResponseStatusException notFoundException =
                new CarRentalResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Map<String, Object>> responseEntity =
                exceptionHandling.handleCarRentalResponseStatusException(notFoundException, webRequest);

        assertEquals(400, responseEntity.getStatusCode().value());
    }

    @Test
    void handleCarRentalExceptionTest() {
        CarRentalException unexpectedError = new CarRentalException("Unexpected Error");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Map<String, Object>> responseEntity =
                exceptionHandling.handleCarRentalException(unexpectedError, webRequest);

        assertEquals(500, responseEntity.getStatusCode().value());
        assertEquals("Unexpected Error", unexpectedError.getMessage());
    }

}
