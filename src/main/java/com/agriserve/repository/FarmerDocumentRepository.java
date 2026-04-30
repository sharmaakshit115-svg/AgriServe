package com.agriserve.repository;

import com.agriserve.entity.FarmerDocument;
import com.agriserve.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmerDocumentRepository extends JpaRepository<FarmerDocument, Long> {

    Page<FarmerDocument> findAllByFarmer_FarmerId(Long farmerId, Pageable pageable);

    Page<FarmerDocument> findAllByVerificationStatus(Status status, Pageable pageable);
}
