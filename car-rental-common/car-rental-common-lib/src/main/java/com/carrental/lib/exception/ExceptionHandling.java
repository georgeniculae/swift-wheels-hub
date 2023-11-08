package com.carrental.lib.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandling extends DefaultErrorAttributes {

    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static final String ERROR = "error";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e, WebRequest request) {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;

        Map<String, Object> errorAttributes =
                getErrorAttributesMap(request, e.getMessage(), e.getLocalizedMessage(), internalServerError);

        return ResponseEntity.status(internalServerError).body(errorAttributes);
    }

    @ExceptionHandler(CarRentalNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(CarRentalNotFoundException e, WebRequest request) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        String cause = "Resource not found";
        Map<String, Object> errorAttributes = getErrorAttributesMap(request, e.getMessage(), cause, notFound);

        return ResponseEntity.status(notFound).body(errorAttributes);
    }

    @ExceptionHandler(CarRentalException.class)
    public ResponseEntity<Map<String, Object>> handleCarRentalException(CarRentalException e, WebRequest request) {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;

        Map<String, Object> errorAttributes =
                getErrorAttributesMap(request, e.getMessage(), e.getMessage(), internalServerError);

        return ResponseEntity.status(internalServerError).body(errorAttributes);
    }

    @ExceptionHandler(CarRentalResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleCarRentalResponseStatusException(CarRentalResponseStatusException e,
                                                                                      WebRequest request) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        Map<String, Object> errorAttributes = getErrorAttributesMap(request, e.getMessage(), e.getReason(), badRequest);

        return ResponseEntity.status(badRequest).body(errorAttributes);
    }

    private Map<String, Object> getErrorAttributesMap(WebRequest webRequest, String errorMessage, String cause,
                                                      HttpStatus httpStatus) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());

        return addErrorAttributes(errorAttributes, errorMessage, cause, httpStatus);
    }

    private Map<String, Object> addErrorAttributes(Map<String, Object> errorAttributes, String errorMessage,
                                                   String cause, HttpStatus httpStatus) {
        errorAttributes.put(MESSAGE, errorMessage);
        errorAttributes.put(STATUS, httpStatus.value());
        errorAttributes.put(ERROR, cause);

        return errorAttributes;
    }

}

