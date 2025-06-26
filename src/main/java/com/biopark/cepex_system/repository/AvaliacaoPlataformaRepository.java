package com.biopark.cepex_system.repository;

import com.biopark.cepex_system.domain.feedback.AvaliacaoPlataforma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AvaliacaoPlataformaRepository extends JpaRepository<AvaliacaoPlataforma, UUID> {
}