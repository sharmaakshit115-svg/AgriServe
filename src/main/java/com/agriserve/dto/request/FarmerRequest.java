package com.agriserve.dto.request;

import com.agriserve.entity.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request DTO for creating or updating a Farmer profile.
 */
@Data
public class FarmerRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String address;

    /**
     * Primary phone number — mandatory, unique, used for duplicate detection.
     * Format: digits only, 10–15 characters.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be 10-15 digits")
    private String phone;

    /**
     * Primary email address — mandatory, unique, used for duplicate detection.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    /** Additional contact info (whatsapp, alternate number) */
    private String contactInfo;

    @Positive(message = "Land size must be positive")
    private Double landSize;

    private String cropType;
}
