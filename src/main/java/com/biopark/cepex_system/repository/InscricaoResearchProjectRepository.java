package com.biopark.cepex_system.repository;

import com.biopark.cepex_system.domain.project.InscricaoResearchProject;
import com.biopark.cepex_system.domain.project.ResearchProject;
import com.biopark.cepex_system.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InscricaoResearchProjectRepository extends JpaRepository<InscricaoResearchProject, UUID> {
    Optional<InscricaoResearchProject> findByResearchProjectAndAluno(ResearchProject researchProject, User aluno);
    List<InscricaoResearchProject> findByResearchProject(ResearchProject researchProject);
    List<InscricaoResearchProject> findByAluno(User aluno);
}