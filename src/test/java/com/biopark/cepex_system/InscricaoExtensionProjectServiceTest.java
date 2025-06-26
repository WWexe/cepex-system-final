package com.biopark.cepex_system;

import com.biopark.cepex_system.domain.project.ExtensionProject;
import com.biopark.cepex_system.domain.project.InscricaoExtensionProject;
import com.biopark.cepex_system.domain.project.StatusInscricaoProjeto;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.domain.user.UserRole;
import com.biopark.cepex_system.repository.ExtensionProjectRepository;
import com.biopark.cepex_system.repository.InscricaoExtensionProjectRepository;
import com.biopark.cepex_system.repository.UserRepository;
import com.biopark.cepex_system.service.InscricaoExtensionProjectService;
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
class InscricaoExtensionProjectServiceTest {

    @Autowired
    private InscricaoExtensionProjectService inscricaoExtensionProjectService;
    @Autowired
    private InscricaoExtensionProjectRepository inscricaoRepository;
    @Autowired
    private ExtensionProjectRepository extensionProjectRepository;
    @Autowired
    private UserRepository userRepository;

    private User coordinator;
    private User student;
    private ExtensionProject extensionProject;

    @BeforeEach
    void setUp() {
        inscricaoRepository.deleteAll();
        extensionProjectRepository.deleteAll();
        userRepository.deleteAll();

        coordinator = new User(UUID.randomUUID(), "coordinator", "password", "coord@example.com", true, UserRole.PROFESSOR);
        userRepository.save(coordinator);

        student = new User(UUID.randomUUID(), "student", "password", "student@example.com", true, UserRole.STUDENT);
        userRepository.save(student);

        extensionProject = new ExtensionProject();
        extensionProject.setId(UUID.randomUUID());
        extensionProject.setTitle("Community Programming");
        extensionProject.setDescription("Teaching basic programming to community youth.");
        extensionProject.setLocation("Community Center");
        extensionProject.setTargetBeneficiaries("Youth aged 14-18");
        extensionProject.setStartDate(LocalDate.now());
        extensionProject.setEndDate(LocalDate.now().plusMonths(6));
        extensionProject.setStatus(ExtensionProject.ProjectStatus.ABERTO);
        extensionProject.setCoordinator(coordinator);
        extensionProject = extensionProjectRepository.save(extensionProject);
    }

    @Test
    @DisplayName("Should allow a student to apply for an extension project")
    void inscreverExtensionProject_Success() {
        InscricaoExtensionProject inscricao = inscricaoExtensionProjectService.inscrever(extensionProject.getId(), student.getId());
        assertNotNull(inscricao);
        assertEquals(extensionProject.getId(), inscricao.getExtensionProject().getId());
        assertEquals(student.getId(), inscricao.getAluno().getId());
        assertEquals(StatusInscricaoProjeto.PENDENTE, inscricao.getStatus());
    }

    @Test
    @DisplayName("Should throw exception if student is already approved for the extension project")
    void inscreverExtensionProject_AlreadyApproved_ThrowsException() {
        InscricaoExtensionProject existingInscricao = new InscricaoExtensionProject(extensionProject, student);
        existingInscricao.setStatus(StatusInscricaoProjeto.APROVADA);
        inscricaoRepository.save(existingInscricao);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                inscricaoExtensionProjectService.inscrever(extensionProject.getId(), student.getId())
        );
        assertTrue(exception.getMessage().contains("Você já está aprovado para este projeto de extensão e não pode se inscrever novamente."));
    }

    @Test
    @DisplayName("Should throw exception if student has a pending application for the extension project")
    void inscreverExtensionProject_AlreadyPending_ThrowsException() {
        InscricaoExtensionProject existingInscricao = new InscricaoExtensionProject(extensionProject, student);
        existingInscricao.setStatus(StatusInscricaoProjeto.PENDENTE);
        inscricaoRepository.save(existingInscricao);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                inscricaoExtensionProjectService.inscrever(extensionProject.getId(), student.getId())
        );
        assertTrue(exception.getMessage().contains("Você já possui uma inscrição pendente para este projeto de extensão."));
    }

    @Test
    @DisplayName("Should allow re-application if previous application was cancelled for extension project")
    void inscreverExtensionProject_AfterCancelled_ReappliesSuccessfully() {
        InscricaoExtensionProject existingInscricao = new InscricaoExtensionProject(extensionProject, student);
        existingInscricao.setStatus(StatusInscricaoProjeto.CANCELADA);
        existingInscricao.setDataInscricao(LocalDateTime.now().minusDays(5)); // Old date
        inscricaoRepository.save(existingInscricao);

        InscricaoExtensionProject reappliedInscricao = inscricaoExtensionProjectService.inscrever(extensionProject.getId(), student.getId());
        assertEquals(StatusInscricaoProjeto.PENDENTE, reappliedInscricao.getStatus());
        assertNotEquals(existingInscricao.getDataInscricao(), reappliedInscricao.getDataInscricao());
    }

    @Test
    @DisplayName("Should allow re-application if previous application was rejected for extension project")
    void inscreverExtensionProject_AfterRejected_ReappliesSuccessfully() {
        InscricaoExtensionProject existingInscricao = new InscricaoExtensionProject(extensionProject, student);
        existingInscricao.setStatus(StatusInscricaoProjeto.REJEITADA);
        existingInscricao.setDataInscricao(LocalDateTime.now().minusDays(5)); // Old date
        inscricaoRepository.save(existingInscricao);

        InscricaoExtensionProject reappliedInscricao = inscricaoExtensionProjectService.inscrever(extensionProject.getId(), student.getId());
        assertEquals(StatusInscricaoProjeto.PENDENTE, reappliedInscricao.getStatus());
        assertNotEquals(existingInscricao.getDataInscricao(), reappliedInscricao.getDataInscricao());
    }

    @Test
    @DisplayName("Should cancel a pending extension project application")
    void cancelarInscricaoExtensionProject_Success() {
        InscricaoExtensionProject inscricao = new InscricaoExtensionProject(extensionProject, student);
        inscricao.setStatus(StatusInscricaoProjeto.PENDENTE);
        inscricaoRepository.save(inscricao);

        inscricaoExtensionProjectService.cancelarInscricao(extensionProject.getId(), student.getId());
        Optional<InscricaoExtensionProject> cancelledInscricao = inscricaoRepository.findById(inscricao.getId());
        assertTrue(cancelledInscricao.isPresent());
        assertEquals(StatusInscricaoProjeto.CANCELADA, cancelledInscricao.get().getStatus());
    }

    @Test
    @DisplayName("Should throw exception if trying to cancel an approved extension project application")
    void cancelarInscricaoExtensionProject_Approved_ThrowsException() {
        InscricaoExtensionProject inscricao = new InscricaoExtensionProject(extensionProject, student);
        inscricao.setStatus(StatusInscricaoProjeto.APROVADA);
        inscricaoRepository.save(inscricao);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                inscricaoExtensionProjectService.cancelarInscricao(extensionProject.getId(), student.getId())
        );
        assertTrue(exception.getMessage().contains("Não é possível cancelar uma inscrição que já foi aprovada."));
    }

    @Test
    @DisplayName("Should return true if student has a pending or approved application for extension project")
    void checkInscricaoStatus_PendingOrApproved_ReturnsTrue() {
        InscricaoExtensionProject pendingInscricao = new InscricaoExtensionProject(extensionProject, student);
        pendingInscricao.setStatus(StatusInscricaoProjeto.PENDENTE);
        inscricaoRepository.save(pendingInscricao);
        assertTrue(inscricaoExtensionProjectService.checkInscricaoStatus(extensionProject.getId(), student.getId()));

        inscricaoRepository.deleteAll(); // Clean for next check
        InscricaoExtensionProject approvedInscricao = new InscricaoExtensionProject(extensionProject, student);
        approvedInscricao.setStatus(StatusInscricaoProjeto.APROVADA);
        inscricaoRepository.save(approvedInscricao);
        assertTrue(inscricaoExtensionProjectService.checkInscricaoStatus(extensionProject.getId(), student.getId()));
    }

    @Test
    @DisplayName("Should return false if student has a rejected or cancelled application for extension project")
    void checkInscricaoStatus_RejectedOrCancelled_ReturnsFalse() {
        InscricaoExtensionProject rejectedInscricao = new InscricaoExtensionProject(extensionProject, student);
        rejectedInscricao.setStatus(StatusInscricaoProjeto.REJEITADA);
        inscricaoRepository.save(rejectedInscricao);
        assertFalse(inscricaoExtensionProjectService.checkInscricaoStatus(extensionProject.getId(), student.getId()));

        inscricaoRepository.deleteAll(); // Clean for next check
        InscricaoExtensionProject cancelledInscricao = new InscricaoExtensionProject(extensionProject, student);
        cancelledInscricao.setStatus(StatusInscricaoProjeto.CANCELADA);
        inscricaoRepository.save(cancelledInscricao);
        assertFalse(inscricaoExtensionProjectService.checkInscricaoStatus(extensionProject.getId(), student.getId()));
    }

    @Test
    @DisplayName("Should return empty list if no applications for the extension project")
    void findInscricoesByExtensionProject_NoApplications_ReturnsEmptyList() {
        List<InscricaoExtensionProject> inscricoes = inscricaoExtensionProjectService.findInscricoesByExtensionProject(extensionProject.getId());
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    @DisplayName("Should return applications for a specific extension project")
    void findInscricoesByExtensionProject_HasApplications_ReturnsList() {
        inscricaoRepository.save(new InscricaoExtensionProject(extensionProject, student));
        User anotherStudent = new User(UUID.randomUUID(), "anotherStudent", "pass", "another@example.com", true, UserRole.STUDENT);
        userRepository.save(anotherStudent);
        inscricaoRepository.save(new InscricaoExtensionProject(extensionProject, anotherStudent));

        List<InscricaoExtensionProject> inscricoes = inscricaoExtensionProjectService.findInscricoesByExtensionProject(extensionProject.getId());
        assertEquals(2, inscricoes.size());
    }

    @Test
    @DisplayName("Should return applications for a specific student for extension projects")
    void findInscricoesByAluno_HasApplications_ReturnsList() {
        inscricaoRepository.save(new InscricaoExtensionProject(extensionProject, student));

        ExtensionProject anotherProject = new ExtensionProject();
        anotherProject.setId(UUID.randomUUID());
        anotherProject.setTitle("Another Extension Project");
        anotherProject.setDescription("Another extension.");
        anotherProject.setLocation("School");
        anotherProject.setTargetBeneficiaries("Children");
        anotherProject.setStartDate(LocalDate.now());
        anotherProject.setEndDate(LocalDate.now().plusMonths(3));
        anotherProject.setStatus(ExtensionProject.ProjectStatus.ABERTO);
        anotherProject.setCoordinator(coordinator);
        extensionProjectRepository.save(anotherProject);

        inscricaoRepository.save(new InscricaoExtensionProject(anotherProject, student));

        List<InscricaoExtensionProject> inscricoes = inscricaoExtensionProjectService.findInscricoesByAluno(student.getId());
        assertEquals(2, inscricoes.size());
    }
}