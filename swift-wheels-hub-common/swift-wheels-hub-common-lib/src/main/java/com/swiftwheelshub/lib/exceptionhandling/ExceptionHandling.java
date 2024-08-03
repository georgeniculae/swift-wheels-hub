package com.swiftwheelshub.lib.exceptionhandling;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandling extends DefaultErrorAttributes {

    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static final String ERROR = "error";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e, WebRequest request) {
        HttpStatus status = getStatus(e);
        String message = getMessage(e);

        Map<String, Object> errorAttributes =
                getErrorAttributesMap(request, message, e.getLocalizedMessage(), status);

        return ResponseEntity.status(status).body(errorAttributes);
    }

    @ExceptionHandler(SwiftWheelsHubNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSwiftWheelsHubNotFoundException(SwiftWheelsHubNotFoundException e,
                                                                                     WebRequest request) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        String cause = "Resource not found";
        Map<String, Object> errorAttributes = getErrorAttributesMap(request, e.getReason(), cause, notFound);

        return ResponseEntity.status(notFound).body(errorAttributes);
    }

    @ExceptionHandler(SwiftWheelsHubException.class)
    public ResponseEntity<Map<String, Object>> handleSwiftWheelsHubException(SwiftWheelsHubException e,
                                                                             WebRequest request) {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;

        Map<String, Object> errorAttributes =
                getErrorAttributesMap(request, e.getMessage(), e.getMessage(), internalServerError);

        return ResponseEntity.status(internalServerError).body(errorAttributes);
    }

    @ExceptionHandler(SwiftWheelsHubResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleSwiftWheelsHubResponseStatusException(SwiftWheelsHubResponseStatusException e,
                                                                                           WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        Map<String, Object> errorAttributes = getErrorAttributesMap(request, e.getReason(), e.getReason(), status);

        return ResponseEntity.status(status).body(errorAttributes);
    }

    private HttpStatus getStatus(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (e instanceof ErrorResponse errorResponse) {
            status = HttpStatus.valueOf(errorResponse.getStatusCode().value());
        }

        if (e instanceof AccessDeniedException) {
            status = HttpStatus.UNAUTHORIZED;
        }

        return status;
    }

    private String getMessage(Exception e) {
        if (e instanceof ResponseStatusException responseStatusException) {
            return responseStatusException.getReason();
        }

        return e.getMessage();
    }

    private Map<String, Object> getErrorAttributesMap(WebRequest webRequest,
                                                      String errorMessage,
                                                      String cause,
                                                      HttpStatus httpStatus) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());

        errorAttributes.put(MESSAGE, errorMessage);
        errorAttributes.put(STATUS, httpStatus.value());
        errorAttributes.put(ERROR, cause);

        return errorAttributes;
    }

}
