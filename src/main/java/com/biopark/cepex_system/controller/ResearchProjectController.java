package com.biopark.cepex_system.controller;

import com.biopark.cepex_system.domain.project.ResearchProject;
import com.biopark.cepex_system.domain.project.InscricaoResearchProject;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.repository.UserRepository;
import com.biopark.cepex_system.service.ResearchProjectService;
import com.biopark.cepex_system.service.InscricaoResearchProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

/**
 * Controlador REST para gerenciar Projetos de Pesquisa.
 * **Novo controlador** que expõe endpoints CRUD e de inscrição específicos para este tipo de projeto.
 * Substitui parte da funcionalidade do ProjetoController genérico.
 */
@RestController
@RequestMapping("/research-projects") // Endpoint base específico para projetos de pesquisa
@CrossOrigin(origins = "*")
public class ResearchProjectController {

    private final ResearchProjectService researchProjectService;
    private final InscricaoResearchProjectService inscricaoResearchProjectService;
    private final UserRepository userRepository;

    public ResearchProjectController(ResearchProjectService researchProjectService, InscricaoResearchProjectService inscricaoResearchProjectService, UserRepository userRepository) {
        this.researchProjectService = researchProjectService;
        this.inscricaoResearchProjectService = inscricaoResearchProjectService;
        this.userRepository = userRepository;
    }

    // Endpoint para criar um novo projeto de pesquisa.
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'COORDENATION', 'SECRETARY')")
    public ResponseEntity<ResearchProject> createResearchProject(@RequestBody @Valid ResearchProject researchProject) {
        try {
            // Carregar leadResearcher do banco de dados
            if (researchProject.getLeadResearcher() != null && researchProject.getLeadResearcher().getId() != null) {
                Optional<User> leadResearcherOpt = userRepository.findById(researchProject.getLeadResearcher().getId());
                if (leadResearcherOpt.isPresent()) {
                    researchProject.setLeadResearcher(leadResearcherOpt.get());
                } else {
                    throw new RuntimeException("Lead Researcher não encontrado com ID: " + researchProject.getLeadResearcher().getId());
                }
            }
            
            // Carregar collaborators do banco de dados
            if (researchProject.getCollaborators() != null && !researchProject.getCollaborators().isEmpty()) {
                Set<User> loadedCollaborators = new HashSet<>();
                for (User collaborator : researchProject.getCollaborators()) {
                    if (collaborator.getId() != null) {
                        Optional<User> userOpt = userRepository.findById(collaborator.getId());
                        if (userOpt.isPresent()) {
                            loadedCollaborators.add(userOpt.get());
                        }
                    }
                }
                researchProject.setCollaborators(loadedCollaborators);
            }
            
            return ResponseEntity.ok(researchProjectService.save(researchProject));
        } catch (Exception e) {
            throw e;
        }
    }

    // Endpoint para listar projetos de pesquisa, com filtros por busca e status.
    @GetMapping
    public ResponseEntity<List<ResearchProject>> getAllResearchProjects(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        List<ResearchProject> projects = researchProjectService.findAll(search, status);
        return ResponseEntity.ok(projects);
    }

    // Endpoint para buscar um projeto de pesquisa por ID.
    @GetMapping("/{id}")
    public ResponseEntity<ResearchProject> getResearchProjectById(@PathVariable UUID id) {
        Optional<ResearchProject> project = researchProjectService.findById(id);
        return project.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para atualizar um projeto de pesquisa existente.
     * **Importante**: Mapeia e atualiza os campos específicos de ResearchProject.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'COORDENATION', 'SECRETARY')")
    public ResponseEntity<ResearchProject> updateResearchProject(@PathVariable UUID id, @RequestBody @Valid ResearchProject projectDetails) {
        System.out.println("Atualizando projeto de pesquisa com ID: " + id);
        System.out.println("Título: " + projectDetails.getTitle());
        System.out.println("LeadResearcher: " + (projectDetails.getLeadResearcher() != null ? projectDetails.getLeadResearcher().getId() : "null"));
        System.out.println("Collaborators: " + (projectDetails.getCollaborators() != null ? projectDetails.getCollaborators().size() : "null"));
        
        return researchProjectService.findById(id)
                .map(existingProject -> {
                    existingProject.setTitle(projectDetails.getTitle());
                    existingProject.setDescription(projectDetails.getDescription());
                    existingProject.setStartDate(projectDetails.getStartDate());
                    existingProject.setEndDate(projectDetails.getEndDate());
                    existingProject.setStatus(projectDetails.getStatus());
                    
                    // Carregar leadResearcher do banco de dados
                    if (projectDetails.getLeadResearcher() != null && projectDetails.getLeadResearcher().getId() != null) {
                        Optional<User> leadResearcherOpt = userRepository.findById(projectDetails.getLeadResearcher().getId());
                        if (leadResearcherOpt.isPresent()) {
                            existingProject.setLeadResearcher(leadResearcherOpt.get());
                            System.out.println("LeadResearcher carregado: " + leadResearcherOpt.get().getLogin());
                        } else {
                            System.out.println("LeadResearcher não encontrado com ID: " + projectDetails.getLeadResearcher().getId());
                        }
                    }
                    
                    // Carregar collaborators do banco de dados
                    if (projectDetails.getCollaborators() != null && !projectDetails.getCollaborators().isEmpty()) {
                        Set<User> loadedCollaborators = new HashSet<>();
                        for (User collaborator : projectDetails.getCollaborators()) {
                            if (collaborator.getId() != null) {
                                Optional<User> userOpt = userRepository.findById(collaborator.getId());
                                if (userOpt.isPresent()) {
                                    loadedCollaborators.add(userOpt.get());
                                    System.out.println("Collaborator carregado: " + userOpt.get().getLogin());
                                } else {
                                    System.out.println("Collaborator não encontrado com ID: " + collaborator.getId());
                                }
                            }
                        }
                        existingProject.setCollaborators(loadedCollaborators);
                    }
                    
                    // Novos campos
                    existingProject.setMaterialUsage(projectDetails.getMaterialUsage());
                    existingProject.setResearchLine(projectDetails.getResearchLine());
                    existingProject.setSubjectTheme(projectDetails.getSubjectTheme());
                    existingProject.setJustification(projectDetails.getJustification());
                    existingProject.setProblemFormulation(projectDetails.getProblemFormulation());
                    existingProject.setHypothesisFormulation(projectDetails.getHypothesisFormulation());
                    existingProject.setGeneralObjective(projectDetails.getGeneralObjective());
                    existingProject.setSpecificObjective(projectDetails.getSpecificObjective());
                    existingProject.setTheoreticalFoundation(projectDetails.getTheoreticalFoundation());
                    existingProject.setMethodologicalApproaches(projectDetails.getMethodologicalApproaches());
                    existingProject.setProjectReferences(projectDetails.getProjectReferences());

                    ResearchProject updatedProject = researchProjectService.save(existingProject);
                    System.out.println("Projeto atualizado com sucesso: " + updatedProject.getId());
                    return ResponseEntity.ok(updatedProject);
                })
                .orElseGet(() -> {
                    System.out.println("Projeto não encontrado com ID: " + id);
                    return ResponseEntity.notFound().build();
                });
    }

    // Endpoint para deletar um projeto de pesquisa por ID.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENATION', 'SECRETARY')")
    public ResponseEntity<Void> deleteResearchProject(@PathVariable UUID id) {
        if (researchProjectService.findById(id).isPresent()) {
            researchProjectService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para um aluno se inscrever em um projeto de pesquisa.
     * **Novo endpoint** com retorno específico para a nova entidade de inscrição.
     * Utiliza o serviço `InscricaoResearchProjectService`.
     */
    @PostMapping("/{projectId}/inscrever")
    public ResponseEntity<InscricaoResearchProject> inscreverResearchProject(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User alunoAutenticado) {

        if (alunoAutenticado == null) {
            return ResponseEntity.status(401).build(); // Não autenticado
        }

        try {
            // Chama o serviço de inscrição específico para projetos de pesquisa.
            InscricaoResearchProject inscricao = inscricaoResearchProjectService.inscrever(projectId, alunoAutenticado.getId());
            return ResponseEntity.ok(inscricao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // Erro na lógica de negócio (ex: já inscrito)
        }
    }

    /**
     * Endpoint para um aluno cancelar sua inscrição em um projeto de pesquisa.
     * **Novo endpoint** que utiliza o serviço `InscricaoResearchProjectService`.
     */
    @DeleteMapping("/{projectId}/inscrever")
    public ResponseEntity<Void> cancelarInscricaoResearchProject(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User alunoAutenticado) {

        if (alunoAutenticado == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            // Chama o serviço para cancelar a inscrição.
            inscricaoResearchProjectService.cancelarInscricao(projectId, alunoAutenticado.getId());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para verificar o status de inscrição de um aluno em um projeto de pesquisa.
     * **Novo endpoint** que utiliza o serviço `InscricaoResearchProjectService`.
     */
    @GetMapping("/{projectId}/inscricao-status")
    public ResponseEntity<Boolean> checkResearchProjectInscricaoStatus(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal User alunoAutenticado) {

        if (alunoAutenticado == null) {
            return ResponseEntity.status(401).build();
        }

        // Chama o serviço para verificar o status.
        boolean hasInscricao = inscricaoResearchProjectService.checkInscricaoStatus(projectId, alunoAutenticado.getId());
        return ResponseEntity.ok(hasInscricao);
    }

    /**
     * Endpoint para listar todas as inscrições de um projeto de pesquisa.
     * **Novo endpoint** que utiliza o serviço `InscricaoResearchProjectService`.
     */
    @GetMapping("/{projectId}/inscricoes")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR', 'COORDENATION', 'SECRETARY')")
    public ResponseEntity<List<InscricaoResearchProject>> getResearchProjectInscricoes(
            @PathVariable UUID projectId) {

        try {
            List<InscricaoResearchProject> inscricoes = inscricaoResearchProjectService.findInscricoesByResearchProject(projectId);
            return ResponseEntity.ok(inscricoes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENATION', 'SECRETARY')")
    public ResponseEntity<ResearchProject> updateProjectStatus(@PathVariable UUID id, @RequestBody Map<String, String> statusUpdate) {
        String newStatus = statusUpdate.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            ResearchProject updatedProject = researchProjectService.updateProjectStatus(id, newStatus);
            return ResponseEntity.ok(updatedProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/inscricoes/aluno/{alunoId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ResearchProject>> getProjetosByAluno(@PathVariable("alunoId") UUID alunoId) {
        User aluno = new User();
        aluno.setId(alunoId);
        List<ResearchProject> projetos = inscricaoResearchProjectService.findProjectsByAluno(aluno);
        return ResponseEntity.ok(projetos);
    }
}