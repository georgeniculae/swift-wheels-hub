package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.RentalOfficeMapper;
import com.swiftwheelshub.agency.repository.RentalOfficeRepository;
import com.swiftwheelshub.dto.RentalOfficeDto;
import com.swiftwheelshub.entity.RentalOffice;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalOfficeService {

    private final RentalOfficeRepository rentalOfficeRepository;
    private final RentalOfficeMapper rentalOfficeMapper;

    public List<RentalOfficeDto> findAllRentalOffices() {
        return rentalOfficeRepository.findAll()
                .stream()
                .map(rentalOfficeMapper::mapEntityToDto)
                .toList();
    }

    public void deleteRentalOfficeById(Long id) {
        rentalOfficeRepository.deleteById(id);
    }

    public RentalOfficeDto findRentalOfficeById(Long id) {
        RentalOffice rentalOffice = findEntityById(id);

        return rentalOfficeMapper.mapEntityToDto(rentalOffice);
    }

    public RentalOffice findEntityById(Long id) {
        return rentalOfficeRepository.findById(id)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Rental office with id " + id + " does not exist"));
    }

    public RentalOfficeDto saveRentalOffice(RentalOfficeDto rentalOfficeDto) {
        RentalOffice rentalOffice = rentalOfficeMapper.mapDtoToEntity(rentalOfficeDto);
        RentalOffice savedRentalOffice = saveEntity(rentalOffice);

        return rentalOfficeMapper.mapEntityToDto(savedRentalOffice);
    }

    public RentalOfficeDto updateRentalOffice(Long id, RentalOfficeDto updatedRentalOfficeDto) {
        RentalOffice existingRentalOffice = findEntityById(id);

        existingRentalOffice.setName(updatedRentalOfficeDto.name());
        existingRentalOffice.setContactAddress(updatedRentalOfficeDto.contactAddress());
        existingRentalOffice.setLogoType(updatedRentalOfficeDto.logoType());

        RentalOffice savedRentalOffice = saveEntity(existingRentalOffice);

        return rentalOfficeMapper.mapEntityToDto(savedRentalOffice);
    }

    public RentalOfficeDto findRentalOfficeByName(String searchString) {
        return rentalOfficeRepository.findRentalOfficeByName(searchString)
                .map(rentalOfficeMapper::mapEntityToDto)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Rental office with name: " + searchString + " does not exist"));
    }

    public Long countRentalOffices() {
        return rentalOfficeRepository.count();
    }

    private RentalOffice saveEntity(RentalOffice existingRentalOffice) {
        return rentalOfficeRepository.saveAndFlush(existingRentalOffice);
    }

}
