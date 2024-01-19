package com.swiftwheelshub.lib.exception;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
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
        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                new SwiftWheelsHubNotFoundException("Resource not found");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Map<String, Object>> responseEntity =
                exceptionHandling.handleSwiftWheelsHubNotFoundException(swiftWheelsHubNotFoundException, webRequest);

        assertEquals(404, responseEntity.getStatusCode().value());
    }

    @Test
    void handleSwiftWheelsHubResponseStatusExceptionTest() {
        SwiftWheelsHubResponseStatusException notFoundException =
                new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Map<String, Object>> responseEntity =
                exceptionHandling.handleSwiftWheelsHubResponseStatusException(notFoundException, webRequest);

        assertEquals(400, responseEntity.getStatusCode().value());
    }

    @Test
    void handleSwiftWheelsHubExceptionTest() {
        SwiftWheelsHubException unexpectedError = new SwiftWheelsHubException("Unexpected Error");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Map<String, Object>> responseEntity =
                exceptionHandling.handleSwiftWheelsHubException(unexpectedError, webRequest);

        assertEquals(500, responseEntity.getStatusCode().value());
        assertEquals("Unexpected Error", unexpectedError.getMessage());
    }

}
