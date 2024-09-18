package com.swiftwheelshub.apigateway.exceptionhandler.handler;

import com.swiftwheelshub.apigateway.exceptionhandler.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.apigateway.exceptionhandler.exception.SwiftWheelsHubResponseStatusException;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@UtilityClass
public class ExceptionUtil {

    public RuntimeException handleException(Throwable e) {
        if (e instanceof WebClientResponseException webClientResponseException) {
            return new SwiftWheelsHubResponseStatusException(
                    webClientResponseException.getStatusCode(),
                    webClientResponseException.getResponseBodyAsString()
            );
        }

        if (e instanceof SwiftWheelsHubResponseStatusException swiftWheelsHubResponseStatusException) {
            return swiftWheelsHubResponseStatusException;
        }

        return new SwiftWheelsHubResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage()
        );
    }

    public static HttpStatusCode extractExceptionStatusCode(Throwable e) {
        if (e instanceof WebClientResponseException webClientResponseException) {
            return webClientResponseException.getStatusCode();
        }

        if (e instanceof SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException) {
            return swiftWheelsHubNotFoundException.getStatusCode();
        }

        if (e instanceof SwiftWheelsHubResponseStatusException swiftWheelsHubResponseStatusException) {
            return swiftWheelsHubResponseStatusException.getStatusCode();
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
