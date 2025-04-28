package com.autohub.agency.mapper;

import com.autohub.agency.util.AssertionUtil;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.AvailableCarInfo;
import com.autohub.dto.CarRequest;
import com.autohub.dto.CarResponse;
import com.autohub.entity.Car;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CarMapperTest {

    private final CarMapper carMapper = new CarMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        CarResponse carResponse = Assertions.assertDoesNotThrow(() -> carMapper.mapEntityToDto(car));

        assertNotNull(carResponse);
        AssertionUtil.assertCarResponse(car, carResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        CarResponse carResponse = Assertions.assertDoesNotThrow(() -> carMapper.mapEntityToDto(null));

        assertNull(carResponse);
    }

    @Test
    void mapDtoToEntityTest_success() {
        MockMultipartFile image =
                new MockMultipartFile("car", "car.jpg", MediaType.TEXT_PLAIN_VALUE, "car".getBytes());

        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        Car car = Assertions.assertDoesNotThrow(() -> carMapper.mapDtoToEntity(carRequest, image));

        assertNotNull(car);
        AssertionUtil.assertCarRequest(car, carRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        Car car = Assertions.assertDoesNotThrow(() -> carMapper.mapDtoToEntity(null, null));

        assertNull(car);
    }

    @Test
    void mapToAvailableCarInfoTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        AvailableCarInfo availableCarInfo = carMapper.mapToAvailableCarInfo(car);

        AssertionUtil.assertAvailableCarInfo(car, availableCarInfo);
    }

    @Test
    void mapToAvailableCarInfoTest_null() {
        assertNull(carMapper.mapToAvailableCarInfo(null));
    }

}
