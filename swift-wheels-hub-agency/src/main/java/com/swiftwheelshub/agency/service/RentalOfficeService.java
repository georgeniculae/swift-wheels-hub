package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.RentalOfficeMapper;
import com.swiftwheelshub.agency.repository.RentalOfficeRepository;
import com.swiftwheelshub.dto.RentalOfficeRequest;
import com.swiftwheelshub.dto.RentalOfficeResponse;
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

    public List<RentalOfficeResponse> findAllRentalOffices() {
        return rentalOfficeRepository.findAll()
                .stream()
                .map(rentalOfficeMapper::mapEntityToDto)
                .toList();
    }

    public void deleteRentalOfficeById(Long id) {
        rentalOfficeRepository.deleteById(id);
    }

    public RentalOfficeResponse findRentalOfficeById(Long id) {
        RentalOffice rentalOffice = findEntityById(id);

        return rentalOfficeMapper.mapEntityToDto(rentalOffice);
    }

    public RentalOffice findEntityById(Long id) {
        return rentalOfficeRepository.findById(id)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Rental office with id " + id + " does not exist"));
    }

    public RentalOfficeResponse saveRentalOffice(RentalOfficeRequest rentalOfficeRequest) {
        RentalOffice rentalOffice = rentalOfficeMapper.mapDtoToEntity(rentalOfficeRequest);
        RentalOffice savedRentalOffice = saveEntity(rentalOffice);

        return rentalOfficeMapper.mapEntityToDto(savedRentalOffice);
    }

    public RentalOfficeResponse updateRentalOffice(Long id, RentalOfficeRequest updatedRentalOfficeRequest) {
        RentalOffice existingRentalOffice = findEntityById(id);

        existingRentalOffice.setName(updatedRentalOfficeRequest.name());
        existingRentalOffice.setContactAddress(updatedRentalOfficeRequest.contactAddress());
        existingRentalOffice.setPhoneNumber(updatedRentalOfficeRequest.phoneNumber());

        RentalOffice savedRentalOffice = saveEntity(existingRentalOffice);

        return rentalOfficeMapper.mapEntityToDto(savedRentalOffice);
    }

    public List<RentalOfficeResponse> findRentalOfficeByFilter(String filter) {
        return rentalOfficeRepository.findRentalOfficeByFilter(filter)
                .stream()
                .map(rentalOfficeMapper::mapEntityToDto)
                .toList();
    }

    public Long countRentalOffices() {
        return rentalOfficeRepository.count();
    }

    private RentalOffice saveEntity(RentalOffice existingRentalOffice) {
        return rentalOfficeRepository.saveAndFlush(existingRentalOffice);
    }

}
