package com.biopark.cepex_system;

import com.biopark.cepex_system.domain.approval.ApprovalItemDTO;
import com.biopark.cepex_system.domain.course.Course;
import com.biopark.cepex_system.domain.course.Discipline;
import com.biopark.cepex_system.domain.monitoria.Monitoria;
import com.biopark.cepex_system.domain.monitoria.SelectionType;
import com.biopark.cepex_system.domain.monitoria.StatusMonitoria;
import com.biopark.cepex_system.domain.professor.Professor;
import com.biopark.cepex_system.domain.project.ExtensionProject;
import com.biopark.cepex_system.domain.project.ResearchProject;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.domain.user.UserRole;
import com.biopark.cepex_system.repository.CourseRepository;
import com.biopark.cepex_system.repository.DisciplineRepository;
import com.biopark.cepex_system.repository.ExtensionProjectRepository;
import com.biopark.cepex_system.repository.MonitoriaRepository;
import com.biopark.cepex_system.repository.ProfessorRepository;
import com.biopark.cepex_system.repository.ResearchProjectRepository;
import com.biopark.cepex_system.repository.UserRepository;
import com.biopark.cepex_system.service.ApprovalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ApprovalServiceTest {

    @Autowired
    private ApprovalService approvalService;
    @Autowired
    private MonitoriaRepository monitoriaRepository;
    @Autowired
    private ResearchProjectRepository researchProjectRepository;
    @Autowired
    private ExtensionProjectRepository extensionProjectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private DisciplineRepository disciplineRepository;

    private Monitoria pendingMonitoria;
    private ResearchProject pendingResearchProject;
    private ExtensionProject pendingExtensionProject;
    private Professor professor;
    private User leadResearcher;
    private User coordinator;
    private Course course;
    private Discipline discipline;


    @BeforeEach
    void setUp() {
        // Clear repositories
        monitoriaRepository.deleteAll();
        researchProjectRepository.deleteAll();
        extensionProjectRepository.deleteAll();
        professorRepository.deleteAll();
        disciplineRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Setup users
        leadResearcher = new User(UUID.randomUUID(), "leadResearcher", "password", "lead@example.com", true, UserRole.PROFESSOR);
        userRepository.save(leadResearcher);

        coordinator = new User(UUID.randomUUID(), "coordinator", "password", "coord@example.com", true, UserRole.PROFESSOR);
        userRepository.save(coordinator);

        User profUser = new User(UUID.randomUUID(), "professorUser", "password", "professor@example.com", true, UserRole.PROFESSOR);
        userRepository.save(profUser);

        professor = new Professor();
        professor.setId(UUID.randomUUID());
        professor.setFirstName("Prof");
        professor.setLastName("Test");
        professor.setEmail("prof.test@biopark.edu.br");
        professor.setRa("P123456");
        professor.setCpf("12345678901");
        professor.setActive(true);
        professor.setUser(profUser);
        professorRepository.save(professor);

        course = new Course(UUID.randomUUID(), "Engenharia de Software", 8, true, LocalDateTime.now());
        courseRepository.save(course);

        discipline = new Discipline(UUID.randomUUID(), "Programação Orientada a Objetos", true, LocalDateTime.now(), course);
        disciplineRepository.save(discipline);


        // Setup pending monitoria
        pendingMonitoria = new Monitoria(
                UUID.randomUUID(),
                "Monitoria de Cálculo I",
                "Auxílio em Cálculo I",
                false,
                "Sala 201",
                5,
                10,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusMonths(3),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(9),
                SelectionType.ENTREVISTA,
                LocalDate.now().plusDays(15),
                "14:00",
                LocalDate.now().plusDays(13),
                StatusMonitoria.PENDENTE,
                course,
                discipline,
                professor
        );
        monitoriaRepository.save(pendingMonitoria);

        // Setup pending research project
        pendingResearchProject = new ResearchProject();
        pendingResearchProject.setId(UUID.randomUUID());
        pendingResearchProject.setTitle("Pesquisa em IA");
        pendingResearchProject.setDescription("Projeto de pesquisa sobre IA");
        pendingResearchProject.setStartDate(LocalDate.now());
        pendingResearchProject.setEndDate(LocalDate.now().plusMonths(6));
        pendingResearchProject.setStatus(ResearchProject.ProjectStatus.ABERTO); // PLANNED status
        pendingResearchProject.setLeadResearcher(leadResearcher);
        researchProjectRepository.save(pendingResearchProject);

        // Setup pending extension project
        pendingExtensionProject = new ExtensionProject();
        pendingExtensionProject.setId(UUID.randomUUID());
        pendingExtensionProject.setTitle("Extensão Comunitária");
        pendingExtensionProject.setDescription("Projeto de extensão na comunidade");
        pendingExtensionProject.setLocation("Centro Comunitário");
        pendingExtensionProject.setTargetBeneficiaries("Crianças");
        pendingExtensionProject.setStartDate(LocalDate.now());
        pendingExtensionProject.setEndDate(LocalDate.now().plusMonths(4));
        pendingExtensionProject.setStatus(ExtensionProject.ProjectStatus.ABERTO); // PLANNED status
        pendingExtensionProject.setCoordinator(coordinator);
        extensionProjectRepository.save(pendingExtensionProject);
    }

    @Test
    @DisplayName("Should return all pending approval items")
    void getPendingApprovals() {
        List<ApprovalItemDTO> approvals = approvalService.getPendingApprovals();
        assertNotNull(approvals);
        assertEquals(3, approvals.size()); // 1 Monitoria, 1 Research Project, 1 Extension Project

        // Verify monitoria
        assertTrue(approvals.stream().anyMatch(
                item -> "MONITORIA".equals(item.type()) &&
                        pendingMonitoria.getId().equals(item.id()) &&
                        "PENDENTE".equals(item.currentStatus())
        ));
        // Verify research project
        assertTrue(approvals.stream().anyMatch(
                item -> "PESQUISA".equals(item.type()) &&
                        pendingResearchProject.getId().equals(item.id()) &&
                        "ABERTO".equals(item.currentStatus())
        ));
        // Verify extension project
        assertTrue(approvals.stream().anyMatch(
                item -> "EXTENSAO".equals(item.type()) &&
                        pendingExtensionProject.getId().equals(item.id()) &&
                        "ABERTO".equals(item.currentStatus())
        ));
    }

    @Test
    @DisplayName("Should approve a pending monitoria")
    void approveMonitoria() {
        approvalService.approveItem(pendingMonitoria.getId(), "MONITORIA");
        Optional<Monitoria> approvedMonitoria = monitoriaRepository.findById(pendingMonitoria.getId());
        assertTrue(approvedMonitoria.isPresent());
        assertEquals(StatusMonitoria.APROVADA, approvedMonitoria.get().getStatusMonitoria());
    }

    @Test
    @DisplayName("Should approve a pending research project")
    void approveResearchProject() {
        approvalService.approveItem(pendingResearchProject.getId(), "PESQUISA");
        Optional<ResearchProject> approvedProject = researchProjectRepository.findById(pendingResearchProject.getId());
        assertTrue(approvedProject.isPresent());
        assertEquals(ResearchProject.ProjectStatus.ANALISE, approvedProject.get().getStatus());
    }

    @Test
    @DisplayName("Should approve a pending extension project")
    void approveExtensionProject() {
        approvalService.approveItem(pendingExtensionProject.getId(), "EXTENSAO");
        Optional<ExtensionProject> approvedProject = extensionProjectRepository.findById(pendingExtensionProject.getId());
        assertTrue(approvedProject.isPresent());
        assertEquals(ExtensionProject.ProjectStatus.ANALISE, approvedProject.get().getStatus());
    }

    @Test
    @DisplayName("Should reject a pending monitoria")
    void rejectMonitoria() {
        approvalService.rejectItem(pendingMonitoria.getId(), "MONITORIA");
        Optional<Monitoria> rejectedMonitoria = monitoriaRepository.findById(pendingMonitoria.getId());
        assertTrue(rejectedMonitoria.isPresent());
        assertEquals(StatusMonitoria.REJEITADA, rejectedMonitoria.get().getStatusMonitoria());
    }

    @Test
    @DisplayName("Should reject a pending research project")
    void rejectResearchProject() {
        approvalService.rejectItem(pendingResearchProject.getId(), "PESQUISA");
        Optional<ResearchProject> rejectedProject = researchProjectRepository.findById(pendingResearchProject.getId());
        assertTrue(rejectedProject.isPresent());
        assertEquals(ResearchProject.ProjectStatus.CANCELADO, rejectedProject.get().getStatus());
    }

    @Test
    @DisplayName("Should reject a pending extension project")
    void rejectExtensionProject() {
        approvalService.rejectItem(pendingExtensionProject.getId(), "EXTENSAO");
        Optional<ExtensionProject> rejectedProject = extensionProjectRepository.findById(pendingExtensionProject.getId());
        assertTrue(rejectedProject.isPresent());
        assertEquals(ExtensionProject.ProjectStatus.CANCELADO, rejectedProject.get().getStatus());
    }

    @Test
    @DisplayName("Should throw exception when approving non-existent item")
    void approveNonExistentItem() {
        UUID nonExistentId = UUID.randomUUID();
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                approvalService.approveItem(nonExistentId, "MONITORIA")
        );
        assertTrue(thrown.getMessage().contains("não encontrada para aprovação."));
    }

    @Test
    @DisplayName("Should throw exception when rejecting non-existent item")
    void rejectNonExistentItem() {
        UUID nonExistentId = UUID.randomUUID();
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                approvalService.rejectItem(nonExistentId, "PESQUISA")
        );
        assertTrue(thrown.getMessage().contains("não encontrado para rejeição."));
    }

    @Test
    @DisplayName("Should throw exception for invalid item type when approving")
    void approveInvalidType() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                approvalService.approveItem(pendingMonitoria.getId(), "INVALID_TYPE")
        );
        assertTrue(thrown.getMessage().contains("Tipo de item inválido para aprovação"));
    }

    @Test
    @DisplayName("Should throw exception for invalid item type when rejecting")
    void rejectInvalidType() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                approvalService.rejectItem(pendingResearchProject.getId(), "INVALID_TYPE")
        );
        assertTrue(thrown.getMessage().contains("Tipo de item inválido para rejeição"));
    }
}