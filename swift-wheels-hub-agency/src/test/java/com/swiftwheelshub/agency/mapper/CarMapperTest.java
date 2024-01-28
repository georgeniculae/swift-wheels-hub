package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.entity.Car;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CarMapperTest {

    private final CarMapper carMapper = new CarMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        CarResponse carResponse = Assertions.assertDoesNotThrow(() -> carMapper.mapEntityToDto(car));

        assertNotNull(carResponse);
        AssertionUtils.assertCarResponse(car, carResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        CarResponse carResponse = Assertions.assertDoesNotThrow(() -> carMapper.mapEntityToDto(null));

        assertNull(carResponse);
    }

    @Test
    void mapDtoToEntityTest_success() {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        Car car = Assertions.assertDoesNotThrow(() -> carMapper.mapDtoToEntity(carRequest));

        assertNotNull(car);
        AssertionUtils.assertCarRequest(car, carRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        Car car = Assertions.assertDoesNotThrow(() -> carMapper.mapDtoToEntity(null));

        assertNull(car);
    }

}
