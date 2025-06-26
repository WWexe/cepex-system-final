package com.biopark.cepex_system.service;

import com.biopark.cepex_system.domain.approval.ApprovalItemDTO;
import com.biopark.cepex_system.domain.monitoria.Monitoria;
import com.biopark.cepex_system.domain.monitoria.StatusMonitoria;
import com.biopark.cepex_system.domain.project.ExtensionProject;
import com.biopark.cepex_system.domain.project.ResearchProject;
import com.biopark.cepex_system.repository.MonitoriaRepository;
import com.biopark.cepex_system.repository.ExtensionProjectRepository;
import com.biopark.cepex_system.repository.ResearchProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ApprovalService {

    private final MonitoriaRepository monitoriaRepository;
    private final ResearchProjectRepository researchProjectRepository;
    private final ExtensionProjectRepository extensionProjectRepository;

    public ApprovalService(MonitoriaRepository monitoriaRepository,
                           ResearchProjectRepository researchProjectRepository,
                           ExtensionProjectRepository extensionProjectRepository) {
        this.monitoriaRepository = monitoriaRepository;
        this.researchProjectRepository = researchProjectRepository;
        this.extensionProjectRepository = extensionProjectRepository;
    }

    /**
     * Retorna uma lista de todos os itens pendentes de aprovação,
     * incluindo Monitorias, Projetos de Pesquisa e Projetos de Extensão.
     * @return List<ApprovalItemDTO> lista de itens pendentes
     */
    public List<ApprovalItemDTO> getPendingApprovals() {
        // Busca e mapeia monitorias com status "PENDENTE"
        List<ApprovalItemDTO> pendingMonitorias = monitoriaRepository.findAll().stream()
                .filter(m -> m.getStatusMonitoria() == StatusMonitoria.PENDENTE)
                .map(this::mapMonitoriaToApprovalItemDTO)
                .collect(Collectors.toList());

        // Busca e mapeia projetos de pesquisa com status "ABERTO"
        List<ApprovalItemDTO> pendingResearchProjects = researchProjectRepository.findAll().stream()
                .filter(p -> p.getStatus() == ResearchProject.ProjectStatus.ABERTO)
                .map(this::mapResearchProjectToApprovalItemDTO)
                .collect(Collectors.toList());

        // Busca e mapeia projetos de extensão com status "ABERTO"
        List<ApprovalItemDTO> pendingExtensionProjects = extensionProjectRepository.findAll().stream()
                .filter(p -> p.getStatus() == ExtensionProject.ProjectStatus.ABERTO)
                .map(this::mapExtensionProjectToApprovalItemDTO)
                .collect(Collectors.toList());

        // Combina todas as listas de itens pendentes em uma única lista
        return Stream.of(pendingMonitorias.stream(), pendingResearchProjects.stream(), pendingExtensionProjects.stream())
                .flatMap(s -> s)
                .collect(Collectors.toList());
    }

    /**
     * Mapeia um objeto Monitoria para um ApprovalItemDTO.
     */
    private ApprovalItemDTO mapMonitoriaToApprovalItemDTO(Monitoria monitoria) {
        String creatorName = (monitoria.getProfessor() != null && monitoria.getProfessor().getUser() != null) ?
                monitoria.getProfessor().getUser().getLogin() : "N/A";
        String department = (monitoria.getSubject() != null) ? monitoria.getSubject().getName() : "N/A";
        LocalDateTime submittedAt = (monitoria.getInicialDate() != null) ? 
                monitoria.getInicialDate().atStartOfDay() : LocalDateTime.now();

        return new ApprovalItemDTO(
                monitoria.getId(),
                monitoria.getTitle(),
                "MONITORIA",
                creatorName,
                department,
                submittedAt,
                "PENDENTE"
        );
    }

    /**
     * Mapeia um objeto ResearchProject para um ApprovalItemDTO.
     */
    private ApprovalItemDTO mapResearchProjectToApprovalItemDTO(ResearchProject project) {
        String creatorName = (project.getLeadResearcher() != null) ?
                project.getLeadResearcher().getLogin() : "N/A";
        String departmentOrArea = project.getResearchLine() != null ? project.getResearchLine() : "N/A";
        LocalDateTime submittedAt = (project.getStartDate() != null) ? 
                project.getStartDate().atStartOfDay() : LocalDateTime.now();

        return new ApprovalItemDTO(
                project.getId(),
                project.getTitle(),
                "PESQUISA",
                creatorName,
                departmentOrArea,
                submittedAt,
                "PENDENTE"
        );
    }

    /**
     * Mapeia um objeto ExtensionProject para um ApprovalItemDTO.
     */
    private ApprovalItemDTO mapExtensionProjectToApprovalItemDTO(ExtensionProject project) {
        String creatorName = (project.getCoordinator() != null) ?
                project.getCoordinator().getLogin() : "N/A";
        String departmentOrArea = project.getLocation() != null ? project.getLocation() : "N/A";
        LocalDateTime submittedAt = (project.getStartDate() != null) ? 
                project.getStartDate().atStartOfDay() : LocalDateTime.now();

        return new ApprovalItemDTO(
                project.getId(),
                project.getTitle(),
                "EXTENSAO",
                creatorName,
                departmentOrArea,
                submittedAt,
                "PENDENTE"
        );
    }

    /**
     * Aprova um item com base no ID e tipo.
     */
    @Transactional
    public void approveItem(UUID id, String type) {
        if ("MONITORIA".equalsIgnoreCase(type)) {
            Monitoria monitoria = monitoriaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Monitoria não encontrada para aprovação."));
            monitoria.setStatusMonitoria(StatusMonitoria.APROVADA);
            monitoriaRepository.save(monitoria);
        } else if ("PESQUISA".equalsIgnoreCase(type)) {
            ResearchProject project = researchProjectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Projeto de Pesquisa não encontrado para aprovação."));
            project.setStatus(ResearchProject.ProjectStatus.ANALISE);
            researchProjectRepository.save(project);
        } else if ("EXTENSAO".equalsIgnoreCase(type)) {
            ExtensionProject project = extensionProjectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Projeto de Extensão não encontrado para aprovação."));
            project.setStatus(ExtensionProject.ProjectStatus.ANALISE);
            extensionProjectRepository.save(project);
        } else {
            throw new IllegalArgumentException("Tipo de item inválido para aprovação: " + type);
        }
    }

    /**
     * Rejeita um item com base no ID e tipo.
     */
    @Transactional
    public void rejectItem(UUID id, String type) {
        if ("MONITORIA".equalsIgnoreCase(type)) {
            Monitoria monitoria = monitoriaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Monitoria não encontrada para rejeição."));
            monitoria.setStatusMonitoria(StatusMonitoria.REJEITADA);
            monitoriaRepository.save(monitoria);
        } else if ("PESQUISA".equalsIgnoreCase(type)) {
            ResearchProject project = researchProjectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Projeto de Pesquisa não encontrado para rejeição."));
            project.setStatus(ResearchProject.ProjectStatus.CANCELADO);
            researchProjectRepository.save(project);
        } else if ("EXTENSAO".equalsIgnoreCase(type)) {
            ExtensionProject project = extensionProjectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Projeto de Extensão não encontrado para rejeição."));
            project.setStatus(ExtensionProject.ProjectStatus.CANCELADO);
            extensionProjectRepository.save(project);
        } else {
            throw new IllegalArgumentException("Tipo de item inválido para rejeição: " + type);
        }
    }
}