package com.swiftwheelshub.customer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@UtilityClass
public class TestUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public static String writeValueAsString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SwiftWheelsHubException(e.getMessage());
        }
    }

    public static <T> T getResourceAsJson(String resourceName, Class<T> valueType) {
        try {
            return OBJECT_MAPPER.readValue(getResourceAsString(resourceName), valueType);
        } catch (JsonProcessingException e) {
            throw new SwiftWheelsHubException("Failed getting resource: " + resourceName + ", cause: " + e.getMessage());
        }
    }

    private static String getResourceAsString(String resourceName) {
        URL resource = TestUtil.class.getResource(resourceName);

        if (resource == null) {
            throw new SwiftWheelsHubException("Failed getting resource: " + resourceName);
        }

        try {
            return new String(Files.readAllBytes(Paths.get(resource.toURI())));
        } catch (IOException | URISyntaxException e) {
            throw new SwiftWheelsHubException("Failed getting resource: " + resourceName);
        }
    }

}
