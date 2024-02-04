package com.swiftwheelshub.agency.controller;

import com.swiftwheelshub.agency.service.BranchService;
import com.swiftwheelshub.dto.BranchRequest;
import com.swiftwheelshub.dto.BranchResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<BranchResponse>> findAllBranches() {
        List<BranchResponse> branchResponses = branchService.findAllBranches();

        return ResponseEntity.ok(branchResponses);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<BranchResponse> findBranchById(@PathVariable("id") Long id) {
        BranchResponse branchResponse = branchService.findBranchById(id);

        return ResponseEntity.ok(branchResponse);
    }

    @GetMapping(path = "/count")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<Long> countBranches() {
        Long numberOfBranches = branchService.countBranches();

        return ResponseEntity.ok(numberOfBranches);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<BranchResponse> addBranch(@RequestBody @Valid BranchRequest branchRequest) {
        BranchResponse savedBranchResponse = branchService.saveBranch(branchRequest);

        return ResponseEntity.ok(savedBranchResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<BranchResponse> updateBranch(@PathVariable("id") Long id, @RequestBody @Valid BranchRequest branchRequest) {
        BranchResponse updatedBranchResponse = branchService.updateBranch(id, branchRequest);

        return ResponseEntity.ok(updatedBranchResponse);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Void> deleteBranchById(@PathVariable("id") Long id) {
        branchService.deleteBranchById(id);

        return ResponseEntity.noContent().build();
    }

}
