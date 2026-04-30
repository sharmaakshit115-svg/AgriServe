package com.agriserve.entity.enums;

/**
 * Roles available in the AgriServe system.
 * Controls access to protected endpoints via Spring Security.
 */
public enum Role {
    FARMER,
    EXTENSION_OFFICER,
    PROGRAM_MANAGER,
    ADMIN,
    COMPLIANCE_OFFICER,
    GOVERNMENT_AUDITOR
}
