package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtil;
import com.swiftwheelshub.dto.RentalOfficeRequest;
import com.swiftwheelshub.dto.RentalOfficeResponse;
import com.swiftwheelshub.entity.RentalOffice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class RentalOfficeMapperTest {

    private final RentalOfficeMapper rentalOfficeMapper = new RentalOfficeMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        RentalOffice rentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeResponse rentalOfficeResponse = rentalOfficeMapper.mapEntityToDto(rentalOffice);

        assertNotNull(rentalOfficeResponse);
        AssertionUtils.assertRentalOfficeResponse(rentalOffice, rentalOfficeResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(rentalOfficeMapper.mapEntityToDto(null));
    }

    @Test
    void mapDtoToEntityTest_success() {
        RentalOfficeRequest rentalOfficeRequest = TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        RentalOffice rentalOffice = assertDoesNotThrow(() -> rentalOfficeMapper.mapDtoToEntity(rentalOfficeRequest));

        assertNotNull(rentalOffice);
        AssertionUtils.assertRentalOfficeRequest(rentalOffice, rentalOfficeRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        assertNull(rentalOfficeMapper.mapDtoToEntity(null));
    }

}
