package com.agriserve.repository;

import com.agriserve.entity.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkshopRepository extends JpaRepository<Workshop, Long> {}