package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.BranchMapper;
import com.swiftwheelshub.agency.repository.BranchRepository;
import com.swiftwheelshub.dto.BranchDto;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.RentalOffice;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final RentalOfficeService rentalOfficeService;
    private final BranchMapper branchMapper;

    public List<BranchDto> findAllBranches() {
        return branchRepository.findAll()
                .stream()
                .map(branchMapper::mapEntityToDto)
                .toList();
    }

    public BranchDto findBranchById(Long id) {
        Branch branch = findEntityById(id);

        return branchMapper.mapEntityToDto(branch);
    }

    public Branch findEntityById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Branch with id " + id + " does not exist"));
    }

    public BranchDto saveBranch(BranchDto branchDto) {
        Branch newBranch = branchMapper.mapDtoToEntity(branchDto);

        newBranch.setRentalOffice(rentalOfficeService.findEntityById(branchDto.rentalOfficeId()));
        Branch savedBranch = saveEntity(newBranch);

        return branchMapper.mapEntityToDto(savedBranch);
    }

    public BranchDto updateBranch(Long id, BranchDto updatedBranchDto) {
        Branch exitingBranch = findEntityById(id);

        Long rentalOfficeId = Objects.requireNonNull(updatedBranchDto.rentalOfficeId());
        RentalOffice rentalOffice = rentalOfficeService.findEntityById(rentalOfficeId);

        exitingBranch.setName(updatedBranchDto.name());
        exitingBranch.setAddress(updatedBranchDto.address());
        exitingBranch.setRentalOffice(rentalOffice);

        Branch savedBranch = saveEntity(exitingBranch);

        return branchMapper.mapEntityToDto(savedBranch);
    }

    public void deleteBranchById(Long id) {
        branchRepository.deleteById(id);
    }

    public BranchDto findBranchByFilter(String searchString) {
        return branchRepository.findByFilter(searchString)
                .map(branchMapper::mapEntityToDto)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Branch with filter: " + searchString + " does not exist"));
    }

    public Long countBranches() {
        return branchRepository.count();
    }

    private Branch saveEntity(Branch branch) {
        return branchRepository.save(branch);
    }

}
