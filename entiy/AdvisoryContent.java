package com.agriservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "advisory_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvisoryContent {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long contentId;

	    @Column(length=150, nullable=false)
	    private String title;

	    private String description;

	    @Column(length=200)
	    private String fileUri;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable=false)
	    private AdvisoryCategory category;

	    private LocalDateTime uploadedDate;

	    @Enumerated(EnumType.STRING)
	    private AdvisoryContentStatus status;
    
        public enum AdvisoryCategory {
    	   CROP,SOIL,MARKET
    	}

       public enum AdvisoryContentStatus {
        ACTIVE,INACTIVE
      } 
}