package com.biopark.cepex_system.service;

import com.biopark.cepex_system.domain.project.ResearchProject;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.repository.ResearchProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResearchProjectService {

    private final ResearchProjectRepository repository;

    public ResearchProjectService(ResearchProjectRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ResearchProject save(ResearchProject researchProject) {
        return repository.save(researchProject);
    }

    /**
     * Metodo de busca e filtragem de projetos de pesquisa.
     * **Importante**: Agora filtra por `title` e `leadResearcher.login`, e por `status` usando o enum `ResearchProject.ProjectStatus`.
     * Trata a conversão de string para enum e ignora valores inválidos.
     */
    public List<ResearchProject> findAll(String search, String status) {
        List<ResearchProject> projects = repository.findAll();

        if (search != null && !search.trim().isEmpty()) {
            String lowerCaseSearch = search.trim().toLowerCase();
            projects = projects.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(lowerCaseSearch) ||
                            (p.getLeadResearcher() != null && p.getLeadResearcher().getLogin().toLowerCase().contains(lowerCaseSearch)))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("TODOS")) {
            try {
                // Conversão de String para Enum ProjectStatus, sensível a erros se a string não for válida.
                ResearchProject.ProjectStatus statusEnum = ResearchProject.ProjectStatus.valueOf(status.toUpperCase());
                projects = projects.stream()
                        .filter(p -> p.getStatus() == statusEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Importante: Lidar com status inválidos, aqui simplesmente ignoramos o filtro.
            }
        }
        return projects;
    }

    public Optional<ResearchProject> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public void delete(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Projeto de Pesquisa não encontrado para o ID: " + id);
        }
    }

    @Transactional
    public ResearchProject updateProjectStatus(UUID id, String newStatusStr) {
        ResearchProject project = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto de Pesquisa não encontrado para o ID: " + id));

        try {
            ResearchProject.ProjectStatus newStatus = ResearchProject.ProjectStatus.valueOf(newStatusStr.toUpperCase());
            project.setStatus(newStatus);
            return repository.save(project);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido fornecido: " + newStatusStr);
        }
    }
}