package com.carrental.agency.service;

import com.carrental.agency.mapper.RentalOfficeMapper;
import com.carrental.agency.repository.RentalOfficeRepository;
import com.carrental.dto.RentalOfficeDto;
import com.carrental.entity.RentalOffice;
import com.carrental.lib.exception.CarRentalNotFoundException;
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
                .orElseThrow(() -> new CarRentalNotFoundException("Rental office with id " + id + " does not exist"));
    }

    public RentalOfficeDto saveRentalOffice(RentalOfficeDto rentalOfficeDto) {
        RentalOffice rentalOffice = rentalOfficeMapper.mapDtoToEntity(rentalOfficeDto);
        RentalOffice savedRentalOffice = saveEntity(rentalOffice);

        return rentalOfficeMapper.mapEntityToDto(savedRentalOffice);
    }

    public RentalOfficeDto updateRentalOffice(Long id, RentalOfficeDto updatedRentalOfficeDto) {
        RentalOffice existingRentalOffice = findEntityById(id);

        existingRentalOffice.setName(updatedRentalOfficeDto.getName());
        existingRentalOffice.setContactAddress(updatedRentalOfficeDto.getContactAddress());
        existingRentalOffice.setLogoType(updatedRentalOfficeDto.getLogoType());

        RentalOffice savedRentalOffice = saveEntity(existingRentalOffice);

        return rentalOfficeMapper.mapEntityToDto(savedRentalOffice);
    }

    public RentalOfficeDto findRentalOfficeByName(String searchString) {
        return rentalOfficeRepository.findRentalOfficeByName(searchString)
                .map(rentalOfficeMapper::mapEntityToDto)
                .orElseThrow(() -> new CarRentalNotFoundException("Rental office with name: " + searchString + " does not exist"));
    }

    public Long countRentalOffices() {
        return rentalOfficeRepository.count();
    }

    private RentalOffice saveEntity(RentalOffice existingRentalOffice) {
        return rentalOfficeRepository.saveAndFlush(existingRentalOffice);
    }

}
