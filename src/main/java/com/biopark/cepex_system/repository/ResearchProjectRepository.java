package com.biopark.cepex_system.repository;

import com.biopark.cepex_system.domain.project.ResearchProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResearchProjectRepository extends JpaRepository<ResearchProject, UUID> {
}