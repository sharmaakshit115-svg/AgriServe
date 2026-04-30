package com.agriserve.dto.response;

import com.agriserve.entity.Farmer;
import com.agriserve.entity.enums.Gender;
import com.agriserve.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Farmer profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmerResponse {

    private Long farmerId;
    private Long userId;
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String phone;
    private String email;
    private String contactInfo;
    private Double landSize;
    private String cropType;
    private Status status;
    private LocalDateTime createdAt;

    public static FarmerResponse from(Farmer farmer) {
        return FarmerResponse.builder()
                .farmerId(farmer.getFarmerId())
                .userId(farmer.getUser() != null ? farmer.getUser().getUserId() : null)
                .name(farmer.getName())
                .dateOfBirth(farmer.getDateOfBirth())
                .gender(farmer.getGender())
                .address(farmer.getAddress())
                .phone(farmer.getPhone())
                .email(farmer.getEmail())
                .contactInfo(farmer.getContactInfo())
                .landSize(farmer.getLandSize())
                .cropType(farmer.getCropType())
                .status(farmer.getStatus())
                .createdAt(farmer.getCreatedAt())
                .build();
    }
}
