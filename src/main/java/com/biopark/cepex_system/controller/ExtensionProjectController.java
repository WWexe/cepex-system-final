package com.biopark.cepex_system.controller;

import com.biopark.cepex_system.domain.project.ExtensionProject;
import com.biopark.cepex_system.domain.project.InscricaoExtensionProject;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.repository.UserRepository;
import com.biopark.cepex_system.service.ExtensionProjectService;
import com.biopark.cepex_system.service.InscricaoExtensionProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

/**
 * Controlador REST para gerenciar Projetos de Extensão.
 * **Novo controlador** que expõe endpoints CRUD e de inscrição específicos para este tipo de projeto.
 * Complementa o ResearchProjectController.
 */
@RestController
@RequestMapping("/extension-projects") // Endpoint base específico para projetos de extensão
@CrossOrigin(origins = "*")
public class ExtensionProjectController {

    private final ExtensionProjectService extensionProjectService;
    private final InscricaoExtensionProjectService inscricaoExtensionProjectService;
    private final UserRepository userRepository;

    public ExtensionProjectController(ExtensionProjectService extensionProjectService, InscricaoExtensionProjectService inscricaoExtensionProjectService, UserRepository userRepository) {
        this.extensionProjectService = extensionProjectService;
        this.inscricaoExtensionProjectService = inscricaoExtensionProjectService;
        this.userRepository = userRepository;
    }

    // Endpoint para criar um novo projeto de extensão.
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'COORDENATION', 'SECRETARY')")
    public ResponseEntity<ExtensionProject> createExtensionProject(@RequestBody @Valid ExtensionProject extensionProject) {
        try {
            // Carregar coordenador do banco de dados
            if (extensionProject.getCoordinator() != null && extensionProject.getCoordinator().getId() != null) {
                Optional<User> coordinatorOpt = userRepository.findById(extensionProject.getCoordinator().getId());
                if (coordinatorOpt.isPresent()) {
                    User coordinator = coordinatorOpt.get();
                    extensionProject.setCoordinator(coordinator);
                    // Definir o leadResearcher como o mesmo usuário do coordenador
                    extensionProject.setLeadResearcher(coordinator);
                } else {
                    throw new RuntimeException("Coordenador não encontrado com ID: " + extensionProject.getCoordinator().getId());
                }
            }
            
            // Carregar membros da equipe do banco de dados
            if (extensionProject.getTeam() != null && !extensionProject.getTeam().isEmpty()) {
                Set<User> loadedTeam = new HashSet<>();
                for (User teamMember : extensionProject.getTeam()) {
                    if (teamMember.getId() != null) {
                        Optional<User> userOpt = userRepository.findById(teamMember.getId());
                        if (userOpt.isPresent()) {
                            loadedTeam.add(userOpt.get());
                        }
                    }
                }
                extensionProject.setTeam(loadedTeam);
            }
            
            // Validar se o status é válido
            if (extensionProject.getStatus() == null) {
                extensionProject.setStatus(ExtensionProject.ProjectStatus.ANALISE);
            }
            
            ExtensionProject savedProject = extensionProjectService.save(extensionProject);
            return ResponseEntity.ok(savedProject);
        } catch (Exception e) {
            throw e;
        }
    }

    // Endpoint para listar projetos de extensão, com filtros por busca e status.
    @GetMapping
    public ResponseEntity<List<ExtensionProject>> getAllExtensionProjects(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        List<ExtensionProject> projects = extensionProjectService.findAll(search, status);
        return ResponseEntity.ok(projects);
    }

    // Endpoint para buscar um projeto de extensão por ID.
    @GetMapping("/{id}")
    public ResponseEntity<ExtensionProject> getExtensionProjectById(@PathVariable UUID id) {
        Optional<ExtensionProject> project = extensionProjectService.findById(id);
        return project.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para atualizar um projeto de extensão existente.
     * **Importante**: Mapeia e atualiza os campos específicos de ExtensionProject.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'COORDENATION', 'SECRETARY')")
    public ResponseEntity<ExtensionProject> updateExtensionProject(@PathVariable UUID id, @RequestBody @Valid ExtensionProject projectDetails) {
        return extensionProjectService.findById(id)
                .map(existingProject -> {
                    existingProject.setTitle(projectDetails.getTitle());
                    existingProject.setDescription(projectDetails.getDescription());
                    existingProject.setLocation(projectDetails.getLocation());
                    existingProject.setTargetBeneficiaries(projectDetails.getTargetBeneficiaries());
                    existingProject.setStartDate(projectDetails.getStartDate());
                    existingProject.setEndDate(projectDetails.getEndDate());
                    existingProject.setStatus(projectDetails.getStatus());
                    
                    // Carregar coordenador do banco de dados
                    if (projectDetails.getCoordinator() != null && projectDetails.getCoordinator().getId() != null) {
                        Optional<User> coordinatorOpt = userRepository.findById(projectDetails.getCoordinator().getId());
                        if (coordinatorOpt.isPresent()) {
                            User coordinator = coordinatorOpt.get();
                            existingProject.setCoordinator(coordinator);
                            // Definir o leadResearcher como o mesmo usuário do coordenador
                            existingProject.setLeadResearcher(coordinator);
                        }
                    }
                    
                    // Carregar membros da equipe do banco de dados
                    if (projectDetails.getTeam() != null && !projectDetails.getTeam().isEmpty()) {
                        Set<User> loadedTeam = new HashSet<>();
                        for (User teamMember : projectDetails.getTeam()) {
                            if (teamMember.getId() != null) {
                                Optional<User> userOpt = userRepository.findById(teamMember.getId());
                                if (userOpt.isPresent()) {
                                    loadedTeam.add(userOpt.get());
                                }
                            }
                        }
                        existingProject.setTeam(loadedTeam);
                    }

                    ExtensionProject updatedProject = extensionProjectService.save(existingProject);
                    return ResponseEntity.ok(updatedProject);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint para deletar um projeto de extensão por ID.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENATION', 'SECRETARY')")
    public ResponseEntity<Void> deleteExtensionProject(@PathVariable UUID id) {
        if (extensionProjectService.findById(id).isPresent()) {
            extensionProjectService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para um aluno se inscrever em um projeto de extensão.
     * **Novo endpoint**, utiliza o serviço `InscricaoExtensionProjectService`.
     */
    @PostMapping("/{projectId}/inscrever")
    public ResponseEntity<InscricaoExtensionProject> inscreverExtensionProject(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User alunoAutenticado) {

        if (alunoAutenticado == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            // Chama o serviço de inscrição específico para projetos de extensão.
            InscricaoExtensionProject inscricao = inscricaoExtensionProjectService.inscrever(projectId, alunoAutenticado.getId());
            return ResponseEntity.ok(inscricao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Endpoint para um aluno cancelar sua inscrição em um projeto de extensão.
     * **Novo endpoint**, utiliza o serviço `InscricaoExtensionProjectService`.
     */
    @DeleteMapping("/{projectId}/inscrever")
    public ResponseEntity<Void> cancelarInscricaoExtensionProject(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User alunoAutenticado) {

        if (alunoAutenticado == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            // Chama o serviço para cancelar a inscrição.
            inscricaoExtensionProjectService.cancelarInscricao(projectId, alunoAutenticado.getId());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para verificar o status de inscrição de um aluno em um projeto de extensão.
     * **Novo endpoint**, utiliza o serviço `InscricaoExtensionProjectService`.
     */
    @GetMapping("/{projectId}/inscricao-status")
    public ResponseEntity<Boolean> checkExtensionProjectInscricaoStatus(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User alunoAutenticado) {

        if (alunoAutenticado == null) {
            return ResponseEntity.status(401).build();
        }

        // Chama o serviço para verificar o status.
        boolean hasInscricao = inscricaoExtensionProjectService.checkInscricaoStatus(projectId, alunoAutenticado.getId());
        return ResponseEntity.ok(hasInscricao);
    }

    /**
     * Endpoint para buscar todas as inscrições de um projeto de extensão.
     * **Novo endpoint**, utiliza o serviço `InscricaoExtensionProjectService`.
     */
    @GetMapping("/{projectId}/inscricoes")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'COORDENATION', 'SECRETARY')")
    public ResponseEntity<List<InscricaoExtensionProject>> getExtensionProjectInscricoes(
            @PathVariable UUID projectId) {

        List<InscricaoExtensionProject> inscricoes = inscricaoExtensionProjectService.findInscricoesByExtensionProject(projectId);
        return ResponseEntity.ok(inscricoes);
    }
}