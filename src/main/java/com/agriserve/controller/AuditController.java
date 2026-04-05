package com.agriserve.controller;

import com.agriserve.dto.AuditDTO;
import com.agriserve.entity.Audit;
import com.agriserve.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audits")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @PostMapping("/submit")
    public ResponseEntity<Audit> submitAudit(@RequestBody AuditDTO dto) {
        Audit audit = auditService.createAudit(dto);
        return new ResponseEntity<>(audit, HttpStatus.CREATED);
    }

    @GetMapping("/officer/{officerId}")
    public ResponseEntity<List<Audit>> getAuditsByOfficer(@PathVariable Long officerId) {
        return ResponseEntity.ok(auditService.getAuditsForOfficer(officerId));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Audit>> getByStatus(@RequestParam String status) {
        List<Audit> audits = auditService.getAuditsByStatus(status);
        return ResponseEntity.ok(audits);
    }

    @PatchMapping("/{auditId}/status")
    public ResponseEntity<Audit> updateStatus(
            @PathVariable Long auditId,
            @RequestParam Audit.AuditStatus status) {
        return ResponseEntity.ok(auditService.updateAuditStatus(auditId, status));
    }
}