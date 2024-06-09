package com.swiftwheelshub.apigateway.exception;

import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
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

}
