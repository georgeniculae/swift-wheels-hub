package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.RentalOfficeMapper;
import com.swiftwheelshub.agency.mapper.RentalOfficeMapperImpl;
import com.swiftwheelshub.agency.repository.RentalOfficeRepository;
import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.RentalOfficeDto;
import com.swiftwheelshub.entity.RentalOffice;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentalOfficeServiceTest {

    @InjectMocks
    private RentalOfficeService rentalOfficeService;

    @Mock
    private RentalOfficeRepository rentalOfficeRepository;

    @Spy
    private RentalOfficeMapper rentalOfficeMapper = new RentalOfficeMapperImpl();

    @Captor
    private ArgumentCaptor<RentalOffice> argumentCaptor = ArgumentCaptor.forClass(RentalOffice.class);

    @Test
    void findAllRentalOfficesTest_success() {
        RentalOffice rentalOffice =
                TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeRepository.findAll()).thenReturn(List.of(rentalOffice));

        List<RentalOfficeDto> rentalOfficeDtoList = assertDoesNotThrow(() -> rentalOfficeService.findAllRentalOffices());
        AssertionUtils.assertRentalOffice(rentalOffice, rentalOfficeDtoList.getFirst());
    }

    @Test
    void findRentalOfficeByIdTest_success() {
        RentalOffice rentalOffice =
                TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeRepository.findById(anyLong())).thenReturn(Optional.of(rentalOffice));

        RentalOfficeDto rentalOfficeDto = assertDoesNotThrow(() -> rentalOfficeService.findRentalOfficeById(1L));
        AssertionUtils.assertRentalOffice(rentalOffice, rentalOfficeDto);
    }

    @Test
    void findRentalOfficeByIdTest_errorOnFindingById() {
        when(rentalOfficeRepository.findById(anyLong())).thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> rentalOfficeService.findRentalOfficeById(1L));

        assertNotNull(swiftWheelsHubNotFoundException);
        assertEquals("Rental office with id 1 does not exist", swiftWheelsHubNotFoundException.getMessage());
    }

    @Test
    void saveRentalOfficeTest_success() {
        RentalOffice rentalOffice =
                TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        when(rentalOfficeRepository.saveAndFlush(any(RentalOffice.class))).thenReturn(rentalOffice);

        RentalOfficeDto savedRentalOfficeDto = assertDoesNotThrow(() -> rentalOfficeService.saveRentalOffice(rentalOfficeDto));
        AssertionUtils.assertRentalOffice(rentalOffice, savedRentalOfficeDto);

        verify(rentalOfficeRepository, times(1)).saveAndFlush(argumentCaptor.capture());
        verify(rentalOfficeMapper, times(1)).mapEntityToDto(any(RentalOffice.class));
    }

    @Test
    void updateRentalOfficeTest_success() {
        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);
        RentalOfficeDto rentalOfficeDto = TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        when(rentalOfficeRepository.findById(anyLong())).thenReturn(Optional.of(rentalOffice));
        when(rentalOfficeRepository.saveAndFlush(any(RentalOffice.class))).thenReturn(rentalOffice);

        RentalOfficeDto updatedRentalOfficeDto =
                assertDoesNotThrow(() -> rentalOfficeService.updateRentalOffice(1L, rentalOfficeDto));

        AssertionUtils.assertRentalOffice(rentalOffice, updatedRentalOfficeDto);
    }

    @Test
    void findRentalOfficeByNameTest_success() {
        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeRepository.findRentalOfficeByName(anyString())).thenReturn(Optional.of(rentalOffice));

        RentalOfficeDto rentalOfficeDto =
                assertDoesNotThrow(() -> rentalOfficeService.findRentalOfficeByName("Test Rental Office"));

        AssertionUtils.assertRentalOffice(rentalOffice, rentalOfficeDto);
    }

    @Test
    void findRentalOfficeByNameTest_errorOnFindingByName() {
        when(rentalOfficeRepository.findRentalOfficeByName(anyString())).thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> rentalOfficeService.findRentalOfficeByName("Test Rental Office"));

        assertNotNull(swiftWheelsHubNotFoundException);
        assertEquals("Rental office with name: Test Rental Office does not exist", swiftWheelsHubNotFoundException.getMessage());
    }

}
