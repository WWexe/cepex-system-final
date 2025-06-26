package com.biopark.cepex_system.service;

import com.biopark.cepex_system.domain.project.ExtensionProject;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.repository.ExtensionProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço para a lógica de negócios de Projetos de Extensão.
 * **Novo serviço**, criado para separar as responsabilidades do antigo ProjetoService.
 */
@Service
public class ExtensionProjectService {

    private final ExtensionProjectRepository repository;

    public ExtensionProjectService(ExtensionProjectRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ExtensionProject save(ExtensionProject extensionProject) {
        return repository.save(extensionProject);
    }

    /**
     * Método de busca e filtragem de projetos de extensão.
     * **Importante**: Filtra por `title` e `coordinator.login`, e por `status` usando o enum `ExtensionProject.ProjectStatus`.
     */
    public List<ExtensionProject> findAll(String search, String status) {
        List<ExtensionProject> projects = repository.findAll();

        if (search != null && !search.trim().isEmpty()) {
            String lowerCaseSearch = search.trim().toLowerCase();
            projects = projects.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(lowerCaseSearch) ||
                            (p.getCoordinator() != null && p.getCoordinator().getLogin().toLowerCase().contains(lowerCaseSearch)))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("TODOS")) {
            try {
                ExtensionProject.ProjectStatus statusEnum = ExtensionProject.ProjectStatus.valueOf(status.toUpperCase());
                projects = projects.stream()
                        .filter(p -> p.getStatus() == statusEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Importante: Lidar com status inválidos, aqui simplesmente ignoramos o filtro.
            }
        }
        return projects;
    }

    public Optional<ExtensionProject> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public void delete(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Projeto de Extensão não encontrado para o ID: " + id);
        }
    }
}