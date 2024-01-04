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

    @PostMapping(path = "/validate")
    public ResponseEntity<RequestValidationReport> validateRequest(@RequestBody HttpServletRequest request) {
        RequestValidationReport requestValidationReport = swaggerRequestValidatorService.validateRequest(request);

        return ResponseEntity.ok(requestValidationReport);
    }

    @DeleteMapping("/invalidate/{microserviceName}")
    public ResponseEntity<Void> invalidateSwaggerCache(@PathVariable("microserviceName") String microserviceName) {
        redisService.repopulateRedisWithSwaggerFolder(microserviceName);

        return ResponseEntity.noContent().build();
    }

}
