package com.carrental.agency.controller;

import com.carrental.agency.service.BranchService;
import com.carrental.dto.BranchDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/branches")
@CrossOrigin(origins = "${cross-origin}")
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    public ResponseEntity<List<BranchDto>> findAllBranches() {
        List<BranchDto> branchDtoList = branchService.findAllBranches();

        return ResponseEntity.ok(branchDtoList);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<BranchDto> findBranchById(@PathVariable("id") Long id) {
        BranchDto branchDto = branchService.findBranchById(id);

        return ResponseEntity.ok(branchDto);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<Long> countBranches() {
        Long numberOfBranches = branchService.countBranches();

        return ResponseEntity.ok(numberOfBranches);
    }

    @PostMapping
    public ResponseEntity<BranchDto> addBranch(@RequestBody @Valid BranchDto branchDto) {
        BranchDto savedBranchDto = branchService.saveBranch(branchDto);

        return ResponseEntity.ok(savedBranchDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchDto> updateBranch(@PathVariable("id") Long id, @RequestBody @Valid BranchDto branchDto) {
        BranchDto updatedBranchDto = branchService.updateBranch(id, branchDto);

        return ResponseEntity.ok(updatedBranchDto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteBranchById(@PathVariable("id") Long id) {
        branchService.deleteBranchById(id);

        return ResponseEntity.noContent().build();
    }

}
