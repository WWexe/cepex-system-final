package com.biopark.cepex_system.repository;

import com.biopark.cepex_system.domain.monitoria.CandidaturaMonitoria;
import com.biopark.cepex_system.domain.monitoria.Monitoria;
import com.biopark.cepex_system.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // Importar List
import java.util.Optional;
import java.util.UUID;

public interface CandidaturaMonitoriaRepository extends JpaRepository<CandidaturaMonitoria, UUID> {
    // Metodo para verificar se um aluno já se candidatou para uma monitoria específica
    Optional<CandidaturaMonitoria> findByMonitoriaAndAluno(Monitoria monitoria, User aluno);

    // NOVO METODO: Para listar candidaturas por Monitoria
    List<CandidaturaMonitoria> findByMonitoria(Monitoria monitoria);

    // NOVO METODO: Para listar candidaturas por Aluno
    List<CandidaturaMonitoria> findByAluno(User aluno);
}