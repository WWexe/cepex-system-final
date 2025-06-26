package com.biopark.cepex_system.repository;

import com.biopark.cepex_system.domain.project.ExtensionProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExtensionProjectRepository extends JpaRepository<ExtensionProject, UUID> {
}
