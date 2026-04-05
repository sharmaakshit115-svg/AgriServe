package com.agriserve.controller;

import com.agriserve.dto.ComplianceDTO;
import com.agriserve.entity.ComplianceRecord;
import com.agriserve.service.ComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compliance")
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceService complianceService;

    @PostMapping("/record")
    public ResponseEntity<ComplianceRecord> createRecord(@RequestBody ComplianceDTO dto) {
        ComplianceRecord record = complianceService.saveCompliance(dto);
        return new ResponseEntity<>(record, HttpStatus.CREATED);
    }


    @GetMapping("/entity/{entityId}/{type}")
    public ResponseEntity<List<ComplianceRecord>> getByEntity(@PathVariable Long entityId,@PathVariable ComplianceRecord.ComplianceType type) {
        return ResponseEntity.ok(complianceService.getRecordsByEntity(entityId,type));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ComplianceRecord>> getByResult(
            @RequestParam ComplianceRecord.ComplianceResult result) {
        return ResponseEntity.ok(complianceService.getRecordsByResult(result));
    }

    @GetMapping("/officer/{userId}")
    public ResponseEntity<List<ComplianceRecord>> getByOfficer(@PathVariable Long userId) {
        return ResponseEntity.ok(complianceService.getRecordsByOfficer(userId));
    }
}
