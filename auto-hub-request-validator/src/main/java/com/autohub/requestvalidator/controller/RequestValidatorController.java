package com.autohub.requestvalidator.controller;

import com.autohub.dto.IncomingRequestDetails;
import com.autohub.dto.RequestValidationReport;
import com.autohub.requestvalidator.service.RedisService;
import com.autohub.requestvalidator.service.SwaggerRequestValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RequestValidatorController {

    private final SwaggerRequestValidatorService swaggerRequestValidatorService;
    private final RedisService redisService;

    @PostMapping(path = "/validate")
    public ResponseEntity<RequestValidationReport> validateRequest(@RequestBody @Validated IncomingRequestDetails request) {
        RequestValidationReport requestValidationReport = swaggerRequestValidatorService.validateRequest(request);

        return ResponseEntity.ok(requestValidationReport);
    }

    @DeleteMapping("/invalidate/{microserviceName}")
    public ResponseEntity<Void> invalidateSwaggerCache(@PathVariable("microserviceName") String microserviceName) {
        redisService.repopulateRedisWithSwaggerFiles(microserviceName);

        return ResponseEntity.noContent().build();
    }

}
