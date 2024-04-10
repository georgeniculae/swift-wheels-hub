package com.swiftwheelshub.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactor.blockhound.BlockHound;

@SpringBootApplication
@EnableDiscoveryClient
public class SwiftWheelsHubApiGatewayApplication {

    public static void main(String[] args) {
        BlockHound.install();
        SpringApplication.run(SwiftWheelsHubApiGatewayApplication.class, args);
    }

}
