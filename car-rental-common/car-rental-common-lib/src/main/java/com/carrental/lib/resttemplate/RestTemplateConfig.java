package com.carrental.lib.resttemplate;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "rest-template", name = "enabled")
public class RestTemplateConfig {

    private final CloseableHttpClient httpClient;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .requestFactory(this::clientHttpRequestFactory)
                .errorHandler(new CustomClientErrorHandler())
                .interceptors(new CustomClientHttpRequestInterceptor())
                .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();

        clientHttpRequestFactory.setHttpClient(httpClient);

        return clientHttpRequestFactory;
    }

}
