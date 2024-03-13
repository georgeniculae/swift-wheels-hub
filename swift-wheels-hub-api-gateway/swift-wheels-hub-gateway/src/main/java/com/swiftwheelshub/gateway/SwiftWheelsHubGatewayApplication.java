package com.swiftwheelshub.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SwiftWheelsHubGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwiftWheelsHubGatewayApplication.class, args);
    }

}
