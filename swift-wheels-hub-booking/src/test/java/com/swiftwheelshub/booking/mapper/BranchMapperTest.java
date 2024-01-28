package com.swiftwheelshub.booking.mapper;

import com.swiftwheelshub.booking.util.AssertionUtils;
import com.swiftwheelshub.booking.util.TestUtils;
import com.swiftwheelshub.dto.BranchRequest;
import com.swiftwheelshub.entity.Branch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class BranchMapperTest {

    private final BranchMapper branchMapper = new BranchMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchRequest branchRequest = assertDoesNotThrow(() -> branchMapper.mapEntityToDto(branch));

        assertNotNull(branchRequest);
        AssertionUtils.assertBranch(branch, branchRequest);
    }

    @Test
    void mapEntityToDtoTest_null() {
        BranchRequest branchRequest = assertDoesNotThrow(() -> branchMapper.mapEntityToDto(null));

        assertNull(branchRequest);
    }

    @Test
    void mapDtoToEntityTest_success() {
        BranchRequest branchRequest = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchRequest.class);

        Branch branch = assertDoesNotThrow(() -> branchMapper.mapDtoToEntity(branchRequest));

        assertNotNull(branch);
        AssertionUtils.assertBranch(branch, branchRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        Branch branch = assertDoesNotThrow(() -> branchMapper.mapDtoToEntity(null));

        assertNull(branch);
    }

}
