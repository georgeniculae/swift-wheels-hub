package com.swiftwheelshub.lib.config.restclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean(name = "loadBalancedRestClientBuilder")
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient restClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder restClientBuilder) {
        return restClientBuilder.requestFactory(getClientHttpRequestFactory())
                .build();
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();

        clientHttpRequestFactory.setConnectTimeout(Duration.ofSeconds(20));
        clientHttpRequestFactory.setConnectionRequestTimeout(Duration.ofSeconds(15));

        return clientHttpRequestFactory;
    }

}
