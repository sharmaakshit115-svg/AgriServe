package com.agriserve.repository;

import com.agriserve.entity.AdvisoryContent;
import com.agriserve.entity.enums.AdvisoryCategory;
import com.agriserve.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvisoryContentRepository extends JpaRepository<AdvisoryContent, Long> {

    Page<AdvisoryContent> findAllByStatus(Status status, Pageable pageable);

    Page<AdvisoryContent> findAllByCategory(AdvisoryCategory category, Pageable pageable);

    @Query("SELECT a FROM AdvisoryContent a WHERE " +
           "(:title IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:category IS NULL OR a.category = :category) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<AdvisoryContent> searchContent(
        @Param("title") String title,
        @Param("category") AdvisoryCategory category,
        @Param("status") Status status,
        Pageable pageable
    );
}
