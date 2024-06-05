package com.swiftwheelshub.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class CarService {

    private static final String SEPARATOR = "/";

    private static final String CAR_STATUS = "carState";

    @Value("${rest-client.url.swift-wheels-hub-agency-cars}")
    private String url;

    private final RestClient restClient;



}
