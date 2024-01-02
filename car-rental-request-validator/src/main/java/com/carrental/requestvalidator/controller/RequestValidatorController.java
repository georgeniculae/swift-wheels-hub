package com.carrental.requestvalidator.controller;

import com.carrental.dto.RequestValidationReport;
import com.carrental.requestvalidator.service.SwaggerRequestValidatorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/request-validator")
public class RequestValidatorController {

    private final SwaggerRequestValidatorService swaggerRequestValidatorService;

    @GetMapping
    public ResponseEntity<RequestValidationReport> validateRequest(HttpServletRequest request) {
        RequestValidationReport requestValidationReport = swaggerRequestValidatorService.filter(request);

        return ResponseEntity.ok(requestValidationReport);
    }

}
