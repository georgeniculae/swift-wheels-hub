package com.swiftwheelshub.servicediscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SwiftWheelsHubServiceDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwiftWheelsHubServiceDiscoveryApplication.class, args);
    }

}
