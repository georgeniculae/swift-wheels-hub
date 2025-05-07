package com.autohub.requestvalidator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.data.redis")
@Getter
@Setter
public class RedisProperties {

    private String host;
    private int port;
    private String password;

}
