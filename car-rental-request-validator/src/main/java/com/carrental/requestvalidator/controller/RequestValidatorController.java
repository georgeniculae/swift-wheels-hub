package com.carrental.requestvalidator.controller;

import com.carrental.dto.RequestValidationReport;
import com.carrental.requestvalidator.service.RedisService;
import com.carrental.requestvalidator.service.SwaggerRequestValidatorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/request-validator")
public class RequestValidatorController {

    private final SwaggerRequestValidatorService swaggerRequestValidatorService;
    private final RedisService redisService;

    @GetMapping
    public ResponseEntity<RequestValidationReport> validateRequest(HttpServletRequest request) {
        RequestValidationReport requestValidationReport = swaggerRequestValidatorService.filter(request);

        return ResponseEntity.ok(requestValidationReport);
    }

    @DeleteMapping
    public ResponseEntity<Void> invalidateSwaggerCache(@PathVariable("microserviceName") String microserviceName) {
        redisService.repopulateRedisWithSwaggerFolder(microserviceName);

        return ResponseEntity.noContent().build();
    }

}
