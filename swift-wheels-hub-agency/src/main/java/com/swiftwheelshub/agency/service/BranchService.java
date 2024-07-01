package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.BranchMapper;
import com.swiftwheelshub.agency.repository.BranchRepository;
import com.swiftwheelshub.dto.BranchRequest;
import com.swiftwheelshub.dto.BranchResponse;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.RentalOffice;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final RentalOfficeService rentalOfficeService;
    private final BranchMapper branchMapper;

    @Transactional(readOnly = true)
    public List<BranchResponse> findAllBranches() {
        try (Stream<Branch> branchStream = branchRepository.findAllBranches()) {
            return branchStream.map(branchMapper::mapEntityToDto).toList();
        }
    }

    public BranchResponse findBranchById(Long id) {
        Branch branch = findEntityById(id);

        return branchMapper.mapEntityToDto(branch);
    }

    public Branch findEntityById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Branch with id " + id + " does not exist"));
    }

    public BranchResponse saveBranch(BranchRequest branchRequest) {
        Branch newBranch = branchMapper.mapDtoToEntity(branchRequest);

        newBranch.setRentalOffice(rentalOfficeService.findEntityById(branchRequest.rentalOfficeId()));
        Branch savedBranch = saveEntity(newBranch);

        return branchMapper.mapEntityToDto(savedBranch);
    }

    public BranchResponse updateBranch(Long id, BranchRequest updatedBranchRequest) {
        Branch exitingBranch = findEntityById(id);

        Long rentalOfficeId = updatedBranchRequest.rentalOfficeId();
        RentalOffice rentalOffice = rentalOfficeService.findEntityById(rentalOfficeId);

        exitingBranch.setName(updatedBranchRequest.name());
        exitingBranch.setAddress(updatedBranchRequest.address());
        exitingBranch.setRentalOffice(rentalOffice);

        Branch savedBranch = saveEntity(exitingBranch);

        return branchMapper.mapEntityToDto(savedBranch);
    }

    public void deleteBranchById(Long id) {
        branchRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> findBranchesByFilter(String filter) {
        try (Stream<Branch> branchesStream = branchRepository.findByFilter(filter)) {
            return branchesStream.map(branchMapper::mapEntityToDto).toList();
        }
    }

    public Long countBranches() {
        return branchRepository.count();
    }

    private Branch saveEntity(Branch branch) {
        return branchRepository.save(branch);
    }

}
