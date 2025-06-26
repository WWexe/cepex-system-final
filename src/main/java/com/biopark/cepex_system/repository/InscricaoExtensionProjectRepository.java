package com.biopark.cepex_system.repository;

import com.biopark.cepex_system.domain.project.ExtensionProject;
import com.biopark.cepex_system.domain.project.InscricaoExtensionProject;
import com.biopark.cepex_system.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InscricaoExtensionProjectRepository extends JpaRepository<InscricaoExtensionProject, UUID> {
    Optional<InscricaoExtensionProject> findByExtensionProjectAndAluno(ExtensionProject extensionProject, User aluno);
    List<InscricaoExtensionProject> findByExtensionProject(ExtensionProject extensionProject);
    List<InscricaoExtensionProject> findByAluno(User aluno);
}