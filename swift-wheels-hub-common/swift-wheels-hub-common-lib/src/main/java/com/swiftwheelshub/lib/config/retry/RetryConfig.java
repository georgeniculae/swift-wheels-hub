package com.swiftwheelshub.lib.config.retry;

import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpStatusCodeException;

@Configuration
public class RetryConfig {

    private static final int MAX_RETRY_ATTEMPTS = 5;
    private final SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(MAX_RETRY_ATTEMPTS);
    private final NeverRetryPolicy neverRetryPolicy = new NeverRetryPolicy();

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
        policy.setExceptionClassifier(configureStatusCodeBasedRetryPolicy());
        retryTemplate.setRetryPolicy(policy);

        return retryTemplate;
    }

    private Classifier<Throwable, RetryPolicy> configureStatusCodeBasedRetryPolicy() {
        return throwable -> {
            if (throwable instanceof HttpStatusCodeException exception) {
                return getRetryPolicyForStatus(HttpStatus.valueOf(exception.getStatusCode().value()));
            }

            return simpleRetryPolicy;
        };
    }

    private RetryPolicy getRetryPolicyForStatus(HttpStatus httpStatus) {
        return switch (httpStatus) {
            case BAD_GATEWAY, SERVICE_UNAVAILABLE, INTERNAL_SERVER_ERROR, GATEWAY_TIMEOUT -> simpleRetryPolicy;
            default -> neverRetryPolicy;
        };
    }

}
