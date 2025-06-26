package com.biopark.cepex_system.repository;


import com.biopark.cepex_system.domain.monitoria.Monitoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MonitoriaRepository extends JpaRepository<Monitoria, UUID> {
}
