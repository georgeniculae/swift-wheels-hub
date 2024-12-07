package com.swiftwheelshub.lib.config.restclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

@Configuration
public class RestClientConfig {

    @Bean(name = "loadBalancedRestClientBuilder")
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient restClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder restClientBuilder) {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = ClientHttpRequestFactoryBuilder.simple()
                .withCustomizers(
                        List.of(
                                requestFactory -> requestFactory.setConnectTimeout(Duration.ofSeconds(60)),
                                requestFactory -> requestFactory.setReadTimeout(Duration.ofSeconds(60))
                        )
                )
                .build();

        return restClientBuilder.requestFactory(clientHttpRequestFactory)
                .build();
    }

}
