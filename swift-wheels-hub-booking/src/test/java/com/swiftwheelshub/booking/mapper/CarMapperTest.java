package com.swiftwheelshub.booking.mapper;

import com.swiftwheelshub.booking.util.AssertionUtils;
import com.swiftwheelshub.booking.util.TestUtils;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.entity.Car;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CarMapperTest {

    private final CarMapper carMapper = new CarMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        CarRequest carRequest = assertDoesNotThrow(() -> carMapper.mapEntityToDto(car));

        assertNotNull(carRequest);
        AssertionUtils.assertCar(car, carRequest);
    }

    @Test
    void mapEntityToDtoTest_null() {
        CarRequest carRequest = assertDoesNotThrow(() -> carMapper.mapEntityToDto(null));

        assertNull(carRequest);
    }

    @Test
    void mapDtoToEntityTest_success() {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarDto.json", CarRequest.class);

        Car car = assertDoesNotThrow(() -> carMapper.mapDtoToEntity(carRequest));

        assertNotNull(car);
        AssertionUtils.assertCar(car, carRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        Car car = assertDoesNotThrow(() -> carMapper.mapDtoToEntity(null));

        assertNull(car);
    }

}
