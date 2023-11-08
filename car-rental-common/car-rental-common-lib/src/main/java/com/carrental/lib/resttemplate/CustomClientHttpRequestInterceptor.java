package com.carrental.lib.resttemplate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

@Slf4j
public class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    @NonNull
    public ClientHttpResponse intercept(HttpRequest request, @NonNull byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
        log.info("URI: {}", request.getURI());
        log.info("HTTP Method: {}", request.getMethod());
        log.info("HTTP Headers: {}", request.getHeaders());

        return execution.execute(request, bytes);
    }

}
