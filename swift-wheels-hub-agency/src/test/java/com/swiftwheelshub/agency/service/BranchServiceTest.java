package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.BranchMapper;
import com.swiftwheelshub.agency.mapper.BranchMapperImpl;
import com.swiftwheelshub.agency.repository.BranchRepository;
import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.BranchDto;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.RentalOffice;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @InjectMocks
    private BranchService branchService;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private RentalOfficeService rentalOfficeService;

    @Spy
    private BranchMapper branchMapper = new BranchMapperImpl();

    @Test
    void findBranchByIdTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchRepository.findById(anyLong())).thenReturn(Optional.of(branch));

        BranchDto actualBranchDto = assertDoesNotThrow(() -> branchService.findBranchById(1L));

        assertNotNull(actualBranchDto);
        verify(branchMapper, times(1)).mapEntityToDto(any(Branch.class));
    }

    @Test
    void findBranchByIdTest_errorOnFindingById() {
        when(branchRepository.findById(anyLong())).thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException = assertThrows(SwiftWheelsHubNotFoundException.class, () -> branchService.findBranchById(1L));

        assertNotNull(swiftWheelsHubNotFoundException);
    }

    @Test
    void updateBranchTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyLong())).thenReturn(rentalOffice);
        when(branchRepository.findById(anyLong())).thenReturn(Optional.of(branch));
        when(branchRepository.save(branch)).thenReturn(branch);

        BranchDto updatedBranchDto = assertDoesNotThrow(() -> branchService.updateBranch(1L, branchDto));
        assertNotNull(updatedBranchDto);
    }

    @Test
    void saveBranchTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyLong())).thenReturn(rentalOffice);
        when(branchRepository.save(any(Branch.class))).thenReturn(branch);

        BranchDto savedBranchDto = assertDoesNotThrow(() -> branchService.saveBranch(branchDto));
        assertThat(savedBranchDto).usingRecursiveAssertion().isEqualTo(branchDto);
    }

    @Test
    void findAllBranchesTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchRepository.findAll()).thenReturn(List.of(branch));

        List<BranchDto> branchDtoList = assertDoesNotThrow(() -> branchService.findAllBranches());
        AssertionUtils.assertBranch(branch, branchDtoList.getFirst());
    }

    @Test
    void findBranchByFilterTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchRepository.findByFilter(anyString())).thenReturn(Optional.of(branch));

        BranchDto branchDto = assertDoesNotThrow(() -> branchService.findBranchByFilter("Test"));
        AssertionUtils.assertBranch(branch, branchDto);
    }

    @Test
    void findBranchByFilterTest_errorOnFindingByFilter() {
        when(branchRepository.findByFilter(anyString())).thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> branchService.findBranchByFilter("Test"));

        assertNotNull(swiftWheelsHubNotFoundException);
        assertEquals("Branch with filter: Test does not exist", swiftWheelsHubNotFoundException.getMessage());
    }

}
