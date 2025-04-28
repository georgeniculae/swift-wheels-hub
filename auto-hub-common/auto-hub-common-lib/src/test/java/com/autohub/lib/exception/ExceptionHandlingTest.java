package com.autohub.lib.exception;

import com.autohub.exception.AutoHubException;
import com.autohub.exception.AutoHubNotFoundException;
import com.autohub.exception.AutoHubResponseStatusException;
import com.autohub.lib.exceptionhandling.ExceptionHandling;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private ExceptionHandling exceptionHandling;

    @Test
    void handleExceptionTest() {
        Exception exception = new Exception("Exception");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Map<String, Object>> responseEntity = exceptionHandling.handleException(exception, webRequest);

        assertEquals(500, responseEntity.getStatusCode().value());
    }

    @Test
    void handleExceptionTest_instanceOfErrorResponse() {
        AutoHubResponseStatusException exception =
                new AutoHubResponseStatusException(HttpStatus.NOT_FOUND, "Not found");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Map<String, Object>> responseEntity = exceptionHandling.handleException(exception, webRequest);

        assertEquals(404, responseEntity.getStatusCode().value());
    }

    @Test
    void handleNotFoundExceptionTest() {
        AutoHubNotFoundException autoHubNotFoundException =
                new AutoHubNotFoundException("Resource not found");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Map<String, Object>> responseEntity =
                exceptionHandling.handleAutoHubNotFoundException(autoHubNotFoundException, webRequest);

        assertEquals(404, responseEntity.getStatusCode().value());
    }

    @Test
    void handleAutoHubResponseStatusExceptionTest() {
        AutoHubResponseStatusException notFoundException =
                new AutoHubResponseStatusException(HttpStatus.BAD_REQUEST, "Resource not found");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Map<String, Object>> responseEntity =
                exceptionHandling.handleAutoHubResponseStatusException(notFoundException, webRequest);

        assertEquals(400, responseEntity.getStatusCode().value());
    }

    @Test
    void handleAutoHubExceptionTest() {
        AutoHubException unexpectedError = new AutoHubException("Unexpected Error");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Map<String, Object>> responseEntity =
                exceptionHandling.handleAutoHubException(unexpectedError, webRequest);

        assertEquals(500, responseEntity.getStatusCode().value());
        assertEquals("Unexpected Error", unexpectedError.getMessage());
    }

}
