package com.carrental.lib.resttemplate;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "rest-template", name = "enabled")
public class CloseableHttpClientConfig {

    @Bean
    public CloseableHttpClient closeableHttpClient() {
        return HttpClientBuilder.create()
                .build();
    }

}
