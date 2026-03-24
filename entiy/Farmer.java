package com.agriservice.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="farmers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farmer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long farmerId;
	
	@Column(nullable=false, length = 150)
	private String name;
	
	private LocalDate dob;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	@Column(columnDefinition = "TEXT")
	private String address;
	
	@Column(length=100)
	private String contactInfo;
	
	private Double landsize;
	
	@Column(length=100)
    private String cropType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private FarmerStatus status;
    
    
    public enum Gender {
        MALE,FEMALE,OTHER
    }

    public enum FarmerStatus {
        ACTIVE,INACTIVE,PENDING
    }

}
