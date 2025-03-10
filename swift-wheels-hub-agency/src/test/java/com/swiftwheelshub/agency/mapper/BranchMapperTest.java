package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.agency.util.AssertionUtil;
import com.swiftwheelshub.agency.util.TestUtil;
import com.swiftwheelshub.dto.BranchRequest;
import com.swiftwheelshub.dto.BranchResponse;
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
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchResponse branchResponse = assertDoesNotThrow(() -> branchMapper.mapEntityToDto(branch));

        assertNotNull(branchResponse);
        AssertionUtil.assertBranchResponse(branch, branchResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(branchMapper.mapEntityToDto(null));
    }

    @Test
    void mapDtoToEntityTest_success() {
        BranchRequest branchRequest = TestUtil.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        Branch branch = assertDoesNotThrow(() -> branchMapper.mapDtoToEntity(branchRequest));

        assertNotNull(branch);
        AssertionUtil.assertBranchRequest(branch, branchRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        assertNull(branchMapper.mapDtoToEntity(null));
    }

}
