package com.swiftwheelshub.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SwiftWheelsHubApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwiftWheelsHubApiGatewayApplication.class, args);
    }

}
