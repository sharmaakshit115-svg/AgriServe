package com.agriserve.dto.response;

import com.agriserve.entity.AdvisoryContent;
import com.agriserve.entity.enums.AdvisoryCategory;
import com.agriserve.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for AdvisoryContent.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvisoryContentResponse {

    private Long contentId;
    private String title;
    private String description;
    private String fileUri;
    private AdvisoryCategory category;
    private LocalDateTime uploadedDate;
    private Status status;
    private Long authorId;
    private String authorName;

    public static AdvisoryContentResponse from(AdvisoryContent content) {
        return AdvisoryContentResponse.builder()
                .contentId(content.getContentId())
                .title(content.getTitle())
                .description(content.getDescription())
                .fileUri(content.getFileUri())
                .category(content.getCategory())
                .uploadedDate(content.getUploadedDate())
                .status(content.getStatus())
                .authorId(content.getAuthor() != null ? content.getAuthor().getUserId() : null)
                .authorName(content.getAuthor() != null ? content.getAuthor().getName() : null)
                .build();
    }
}
