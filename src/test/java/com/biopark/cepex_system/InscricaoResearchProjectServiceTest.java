package com.biopark.cepex_system;

import com.biopark.cepex_system.domain.project.InscricaoResearchProject;
import com.biopark.cepex_system.domain.project.ResearchProject;
import com.biopark.cepex_system.domain.project.StatusInscricaoProjeto;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.domain.user.UserRole;
import com.biopark.cepex_system.repository.InscricaoResearchProjectRepository;
import com.biopark.cepex_system.repository.ResearchProjectRepository;
import com.biopark.cepex_system.repository.UserRepository;
import com.biopark.cepex_system.service.InscricaoResearchProjectService;
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
class InscricaoResearchProjectServiceTest {

    @Autowired
    private InscricaoResearchProjectService inscricaoResearchProjectService;
    @Autowired
    private InscricaoResearchProjectRepository inscricaoRepository;
    @Autowired
    private ResearchProjectRepository researchProjectRepository;
    @Autowired
    private UserRepository userRepository;

    private User leadResearcher;
    private User student;
    private ResearchProject researchProject;

    @BeforeEach
    void setUp() {
        inscricaoRepository.deleteAll();
        researchProjectRepository.deleteAll();
        userRepository.deleteAll();

        leadResearcher = new User(UUID.randomUUID(), "leadResearcher", "password", "lead@example.com", true, UserRole.PROFESSOR);
        userRepository.save(leadResearcher);

        student = new User(UUID.randomUUID(), "student", "password", "student@example.com", true, UserRole.STUDENT);
        userRepository.save(student);

        researchProject = new ResearchProject();
        researchProject.setId(UUID.randomUUID());
        researchProject.setTitle("AI in Education");
        researchProject.setDescription("Research on AI applications in education.");
        researchProject.setStartDate(LocalDate.now());
        researchProject.setEndDate(LocalDate.now().plusMonths(6));
        researchProject.setStatus(ResearchProject.ProjectStatus.ABERTO);
        researchProject.setLeadResearcher(leadResearcher);
        researchProject = researchProjectRepository.save(researchProject);
    }

    @Test
    @DisplayName("Should allow a student to apply for a research project")
    void inscreverResearchProject_Success() {
        InscricaoResearchProject inscricao = inscricaoResearchProjectService.inscrever(researchProject.getId(), student.getId());
        assertNotNull(inscricao);
        assertEquals(researchProject.getId(), inscricao.getResearchProject().getId());
        assertEquals(student.getId(), inscricao.getAluno().getId());
        assertEquals(StatusInscricaoProjeto.PENDENTE, inscricao.getStatus());
    }

    @Test
    @DisplayName("Should throw exception if student is already approved for the project")
    void inscreverResearchProject_AlreadyApproved_ThrowsException() {
        InscricaoResearchProject existingInscricao = new InscricaoResearchProject(researchProject, student);
        existingInscricao.setStatus(StatusInscricaoProjeto.APROVADA);
        inscricaoRepository.save(existingInscricao);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                inscricaoResearchProjectService.inscrever(researchProject.getId(), student.getId())
        );
        assertTrue(exception.getMessage().contains("Você já está aprovado para este projeto de pesquisa e não pode se inscrever novamente."));
    }

    @Test
    @DisplayName("Should throw exception if student has a pending application for the project")
    void inscreverResearchProject_AlreadyPending_ThrowsException() {
        InscricaoResearchProject existingInscricao = new InscricaoResearchProject(researchProject, student);
        existingInscricao.setStatus(StatusInscricaoProjeto.PENDENTE);
        inscricaoRepository.save(existingInscricao);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                inscricaoResearchProjectService.inscrever(researchProject.getId(), student.getId())
        );
        assertTrue(exception.getMessage().contains("Você já possui uma inscrição pendente para este projeto de pesquisa."));
    }

    @Test
    @DisplayName("Should allow re-application if previous application was cancelled")
    void inscreverResearchProject_AfterCancelled_ReappliesSuccessfully() {
        InscricaoResearchProject existingInscricao = new InscricaoResearchProject(researchProject, student);
        existingInscricao.setStatus(StatusInscricaoProjeto.CANCELADA);
        existingInscricao.setDataInscricao(LocalDateTime.now().minusDays(5)); // Old date
        inscricaoRepository.save(existingInscricao);

        InscricaoResearchProject reappliedInscricao = inscricaoResearchProjectService.inscrever(researchProject.getId(), student.getId());
        assertEquals(StatusInscricaoProjeto.PENDENTE, reappliedInscricao.getStatus());
        assertNotEquals(existingInscricao.getDataInscricao(), reappliedInscricao.getDataInscricao());
    }

    @Test
    @DisplayName("Should allow re-application if previous application was rejected")
    void inscreverResearchProject_AfterRejected_ReappliesSuccessfully() {
        InscricaoResearchProject existingInscricao = new InscricaoResearchProject(researchProject, student);
        existingInscricao.setStatus(StatusInscricaoProjeto.REJEITADA);
        existingInscricao.setDataInscricao(LocalDateTime.now().minusDays(5)); // Old date
        inscricaoRepository.save(existingInscricao);

        InscricaoResearchProject reappliedInscricao = inscricaoResearchProjectService.inscrever(researchProject.getId(), student.getId());
        assertEquals(StatusInscricaoProjeto.PENDENTE, reappliedInscricao.getStatus());
        assertNotEquals(existingInscricao.getDataInscricao(), reappliedInscricao.getDataInscricao());
    }

    @Test
    @DisplayName("Should cancel a pending research project application")
    void cancelarInscricaoResearchProject_Success() {
        InscricaoResearchProject inscricao = new InscricaoResearchProject(researchProject, student);
        inscricao.setStatus(StatusInscricaoProjeto.PENDENTE);
        inscricaoRepository.save(inscricao);

        inscricaoResearchProjectService.cancelarInscricao(researchProject.getId(), student.getId());
        Optional<InscricaoResearchProject> cancelledInscricao = inscricaoRepository.findById(inscricao.getId());
        assertTrue(cancelledInscricao.isPresent());
        assertEquals(StatusInscricaoProjeto.CANCELADA, cancelledInscricao.get().getStatus());
    }

    @Test
    @DisplayName("Should throw exception if trying to cancel an approved research project application")
    void cancelarInscricaoResearchProject_Approved_ThrowsException() {
        InscricaoResearchProject inscricao = new InscricaoResearchProject(researchProject, student);
        inscricao.setStatus(StatusInscricaoProjeto.APROVADA);
        inscricaoRepository.save(inscricao);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                inscricaoResearchProjectService.cancelarInscricao(researchProject.getId(), student.getId())
        );
        assertTrue(exception.getMessage().contains("Não é possível cancelar uma inscrição que já foi aprovada."));
    }

    @Test
    @DisplayName("Should return true if student has a pending or approved application for research project")
    void checkInscricaoStatus_PendingOrApproved_ReturnsTrue() {
        InscricaoResearchProject pendingInscricao = new InscricaoResearchProject(researchProject, student);
        pendingInscricao.setStatus(StatusInscricaoProjeto.PENDENTE);
        inscricaoRepository.save(pendingInscricao);
        assertTrue(inscricaoResearchProjectService.checkInscricaoStatus(researchProject.getId(), student.getId()));

        inscricaoRepository.deleteAll(); // Clean for next check
        InscricaoResearchProject approvedInscricao = new InscricaoResearchProject(researchProject, student);
        approvedInscricao.setStatus(StatusInscricaoProjeto.APROVADA);
        inscricaoRepository.save(approvedInscricao);
        assertTrue(inscricaoResearchProjectService.checkInscricaoStatus(researchProject.getId(), student.getId()));
    }

    @Test
    @DisplayName("Should return false if student has a rejected or cancelled application for research project")
    void checkInscricaoStatus_RejectedOrCancelled_ReturnsFalse() {
        InscricaoResearchProject rejectedInscricao = new InscricaoResearchProject(researchProject, student);
        rejectedInscricao.setStatus(StatusInscricaoProjeto.REJEITADA);
        inscricaoRepository.save(rejectedInscricao);
        assertFalse(inscricaoResearchProjectService.checkInscricaoStatus(researchProject.getId(), student.getId()));

        inscricaoRepository.deleteAll(); // Clean for next check
        InscricaoResearchProject cancelledInscricao = new InscricaoResearchProject(researchProject, student);
        cancelledInscricao.setStatus(StatusInscricaoProjeto.CANCELADA);
        inscricaoRepository.save(cancelledInscricao);
        assertFalse(inscricaoResearchProjectService.checkInscricaoStatus(researchProject.getId(), student.getId()));
    }

    @Test
    @DisplayName("Should return empty list if no applications for the project")
    void findInscricoesByResearchProject_NoApplications_ReturnsEmptyList() {
        List<InscricaoResearchProject> inscricoes = inscricaoResearchProjectService.findInscricoesByResearchProject(researchProject.getId());
        assertTrue(inscricoes.isEmpty());
    }

    @Test
    @DisplayName("Should return applications for a specific research project")
    void findInscricoesByResearchProject_HasApplications_ReturnsList() {
        inscricaoRepository.save(new InscricaoResearchProject(researchProject, student));
        User anotherStudent = new User(UUID.randomUUID(), "anotherStudent", "pass", "another@example.com", true, UserRole.STUDENT);
        userRepository.save(anotherStudent);
        inscricaoRepository.save(new InscricaoResearchProject(researchProject, anotherStudent));

        List<InscricaoResearchProject> inscricoes = inscricaoResearchProjectService.findInscricoesByResearchProject(researchProject.getId());
        assertEquals(2, inscricoes.size());
    }

    @Test
    @DisplayName("Should return applications for a specific student")
    void findInscricoesByAluno_HasApplications_ReturnsList() {
        inscricaoRepository.save(new InscricaoResearchProject(researchProject, student));

        ResearchProject anotherProject = new ResearchProject();
        anotherProject.setId(UUID.randomUUID());
        anotherProject.setTitle("Another Research Project");
        anotherProject.setDescription("Another research.");
        anotherProject.setStartDate(LocalDate.now());
        anotherProject.setEndDate(LocalDate.now().plusMonths(3));
        anotherProject.setStatus(ResearchProject.ProjectStatus.ABERTO);
        anotherProject.setLeadResearcher(leadResearcher);
        researchProjectRepository.save(anotherProject);

        inscricaoRepository.save(new InscricaoResearchProject(anotherProject, student));

        List<InscricaoResearchProject> inscricoes = inscricaoResearchProjectService.findInscricoesByAluno(student.getId());
        assertEquals(2, inscricoes.size());
    }
}