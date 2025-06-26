package com.biopark.cepex_system;

import com.biopark.cepex_system.domain.course.Course;
import com.biopark.cepex_system.domain.course.Discipline;
import com.biopark.cepex_system.domain.monitoria.CandidaturaMonitoria;
import com.biopark.cepex_system.domain.monitoria.Monitoria;
import com.biopark.cepex_system.domain.monitoria.SelectionType;
import com.biopark.cepex_system.domain.monitoria.StatusCandidatura;
import com.biopark.cepex_system.domain.monitoria.StatusMonitoria;
import com.biopark.cepex_system.domain.professor.Professor;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.domain.user.UserRole;
import com.biopark.cepex_system.repository.CandidaturaMonitoriaRepository;
import com.biopark.cepex_system.repository.CourseRepository;
import com.biopark.cepex_system.repository.DisciplineRepository;
import com.biopark.cepex_system.repository.MonitoriaRepository;
import com.biopark.cepex_system.repository.ProfessorRepository;
import com.biopark.cepex_system.repository.UserRepository;
import com.biopark.cepex_system.service.CandidaturaMonitoriaService;
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
class CandidaturaMonitoriaServiceTest {

    @Autowired
    private CandidaturaMonitoriaService candidaturaMonitoriaService;
    @Autowired
    private CandidaturaMonitoriaRepository candidaturaRepository;
    @Autowired
    private MonitoriaRepository monitoriaRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private DisciplineRepository disciplineRepository;

    private User professorUser;
    private Professor professor;
    private User studentUser;
    private Course course;
    private Discipline discipline;
    private Monitoria monitoria;

    @BeforeEach
    void setUp() {
        // Clear repositories (ensure a clean state for each test)
        candidaturaRepository.deleteAll();
        monitoriaRepository.deleteAll();
        professorRepository.deleteAll();
        disciplineRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Setup common test data
        professorUser = new User("professorTest", "professorTest@example.com", "password", UserRole.PROFESSOR);
        professorUser.setStatus(true);
        professorUser = userRepository.save(professorUser);

        professor = new Professor();
        professor.setFirstName("Prof");
        professor.setLastName("Test");
        professor.setEmail("prof.test@biopark.edu.br");
        professor.setRa("P123456");
        professor.setCpf("12345678901");
        professor.setActive(true);
        professor.setUser(professorUser);
        professor = professorRepository.save(professor);

        studentUser = new User("studentTest", "studentTest@example.com", "password", UserRole.STUDENT);
        studentUser.setStatus(true);
        studentUser = userRepository.save(studentUser);

        course = new Course(null, "Engenharia de Software Test", 8, true, LocalDateTime.now());
        course = courseRepository.save(course);

        discipline = new Discipline(null, "Programação Orientada a Objetos Test", true, LocalDateTime.now(), course);
        discipline = disciplineRepository.save(discipline);

        monitoria = new Monitoria(
                null,
                "Monitoria de POO",
                "Descrição da Monitoria de POO",
                false,
                "Laboratório 101",
                2,
                20,
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
        monitoria = monitoriaRepository.save(monitoria);
    }

    @Test
    @DisplayName("Should create a new candidature successfully")
    void candidatarSuccess() {
        CandidaturaMonitoria candidatura = candidaturaMonitoriaService.candidatar(monitoria.getId(), studentUser.getId());
        assertNotNull(candidatura.getId());
        assertEquals(monitoria.getId(), candidatura.getMonitoria().getId());
        assertEquals(studentUser.getId(), candidatura.getAluno().getId());
        assertEquals(StatusCandidatura.PENDENTE, candidatura.getStatus());
    }

    @Test
    @DisplayName("Should throw exception if student tries to apply for an approved monitoria again")
    void candidatarAlreadyApproved() {
        CandidaturaMonitoria existingCandidatura = new CandidaturaMonitoria(monitoria, studentUser);
        existingCandidatura.setStatus(StatusCandidatura.APROVADA);
        candidaturaRepository.save(existingCandidatura);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                candidaturaMonitoriaService.candidatar(monitoria.getId(), studentUser.getId())
        );
        assertTrue(thrown.getMessage().contains("Você já foi aprovado para esta monitoria e não pode se candidatar novamente."));
    }

    @Test
    @DisplayName("Should throw exception if student has a pending candidature")
    void candidatarAlreadyPending() {
        CandidaturaMonitoria existingCandidatura = new CandidaturaMonitoria(monitoria, studentUser);
        existingCandidatura.setStatus(StatusCandidatura.PENDENTE);
        candidaturaRepository.save(existingCandidatura);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                candidaturaMonitoriaService.candidatar(monitoria.getId(), studentUser.getId())
        );
        assertTrue(thrown.getMessage().contains("Você já possui uma candidatura pendente para esta monitoria."));
    }

    @Test
    @DisplayName("Should allow re-application if previous candidature was cancelled")
    void candidatarAfterCancelled() {
        CandidaturaMonitoria existingCandidatura = new CandidaturaMonitoria(monitoria, studentUser);
        existingCandidatura.setStatus(StatusCandidatura.CANCELADA);
        candidaturaRepository.save(existingCandidatura);

        CandidaturaMonitoria newCandidatura = candidaturaMonitoriaService.candidatar(monitoria.getId(), studentUser.getId());
        assertEquals(StatusCandidatura.PENDENTE, newCandidatura.getStatus());
        assertNotEquals(existingCandidatura.getDataCandidatura(), newCandidatura.getDataCandidatura());
    }

    @Test
    @DisplayName("Should allow re-application if previous candidature was rejected")
    void candidatarAfterRejected() {
        CandidaturaMonitoria existingCandidatura = new CandidaturaMonitoria(monitoria, studentUser);
        existingCandidatura.setStatus(StatusCandidatura.REJEITADA);
        candidaturaRepository.save(existingCandidatura);

        CandidaturaMonitoria newCandidatura = candidaturaMonitoriaService.candidatar(monitoria.getId(), studentUser.getId());
        assertEquals(StatusCandidatura.PENDENTE, newCandidatura.getStatus());
        assertNotEquals(existingCandidatura.getDataCandidatura(), newCandidatura.getDataCandidatura());
    }

    @Test
    @DisplayName("Should cancel a pending candidature successfully")
    void cancelarCandidaturaSuccess() {
        CandidaturaMonitoria candidatura = new CandidaturaMonitoria(monitoria, studentUser);
        candidaturaRepository.save(candidatura);

        candidaturaMonitoriaService.cancelarCandidatura(monitoria.getId(), studentUser.getId());

        Optional<CandidaturaMonitoria> cancelledCandidatura = candidaturaRepository.findById(candidatura.getId());
        assertTrue(cancelledCandidatura.isPresent());
        assertEquals(StatusCandidatura.CANCELADA, cancelledCandidatura.get().getStatus());
    }

    @Test
    @DisplayName("Should throw exception when cancelling an approved candidature")
    void cancelarCandidaturaApproved() {
        CandidaturaMonitoria candidatura = new CandidaturaMonitoria(monitoria, studentUser);
        candidatura.setStatus(StatusCandidatura.APROVADA);
        candidaturaRepository.save(candidatura);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                candidaturaMonitoriaService.cancelarCandidatura(monitoria.getId(), studentUser.getId())
        );
        assertTrue(thrown.getMessage().contains("Não é possível cancelar uma candidatura que já foi aprovada."));
    }

    @Test
    @DisplayName("Should check candidature status as true for pending")
    void checkCandidaturaStatusPending() {
        CandidaturaMonitoria candidatura = new CandidaturaMonitoria(monitoria, studentUser);
        candidatura.setStatus(StatusCandidatura.PENDENTE);
        candidaturaRepository.save(candidatura);

        boolean hasCandidatura = candidaturaMonitoriaService.checkCandidaturaStatus(monitoria.getId(), studentUser.getId());
        assertTrue(hasCandidatura);
    }

    @Test
    @DisplayName("Should check candidature status as true for approved")
    void checkCandidaturaStatusApproved() {
        CandidaturaMonitoria candidature = new CandidaturaMonitoria(monitoria, studentUser);
        candidature.setStatus(StatusCandidatura.APROVADA);
        candidaturaRepository.save(candidature);

        boolean hasCandidature = candidaturaMonitoriaService.checkCandidaturaStatus(monitoria.getId(), studentUser.getId());
        assertTrue(hasCandidature);
    }

    @Test
    @DisplayName("Should check candidature status as false for rejected")
    void checkCandidaturaStatusRejected() {
        CandidaturaMonitoria candidature = new CandidaturaMonitoria(monitoria, studentUser);
        candidature.setStatus(StatusCandidatura.REJEITADA);
        candidaturaRepository.save(candidature);

        boolean hasCandidature = candidaturaMonitoriaService.checkCandidaturaStatus(monitoria.getId(), studentUser.getId());
        assertFalse(hasCandidature);
    }

    @Test
    @DisplayName("Should find candidatures by monitoria")
    void findCandidaturasByMonitoria() {
        CandidaturaMonitoria candidatura1 = new CandidaturaMonitoria(monitoria, studentUser);
        candidaturaRepository.save(candidatura1);

        User anotherStudentUser = new User("student2", "student2@example.com", "password", UserRole.STUDENT);
        anotherStudentUser.setStatus(true);
        anotherStudentUser = userRepository.save(anotherStudentUser);
        CandidaturaMonitoria candidatura2 = new CandidaturaMonitoria(monitoria, anotherStudentUser);
        candidaturaRepository.save(candidatura2);

        List<CandidaturaMonitoria> candidaturas = candidaturaMonitoriaService.findCandidaturasByMonitoria(monitoria.getId());
        assertFalse(candidaturas.isEmpty());
        assertEquals(2, candidaturas.size());
    }

    @Test
    @DisplayName("Should find candidatures by student")
    void findCandidaturasByAluno() {
        CandidaturaMonitoria candidatura1 = new CandidaturaMonitoria(monitoria, studentUser);
        candidaturaRepository.save(candidatura1);

        Monitoria anotherMonitoria = new Monitoria(
                null,
                "Monitoria de Estruturas de Dados",
                "Descrição",
                false,
                "Lab 102",
                1,
                10,
                LocalDate.now().plusDays(20),
                LocalDate.now().plusMonths(4),
                LocalDate.now().plusDays(15),
                LocalDate.now().plusDays(19),
                SelectionType.ENTREVISTA,
                LocalDate.now().plusDays(25),
                "10:00",
                LocalDate.now().plusDays(23),
                StatusMonitoria.PENDENTE,
                course,
                discipline,
                professor
        );
        anotherMonitoria = monitoriaRepository.save(anotherMonitoria);

        CandidaturaMonitoria candidatura2 = new CandidaturaMonitoria(anotherMonitoria, studentUser);
        candidaturaRepository.save(candidatura2);

        List<CandidaturaMonitoria> candidaturas = candidaturaMonitoriaService.findCandidaturasByAluno(studentUser.getId());
        assertFalse(candidaturas.isEmpty());
        assertEquals(2, candidaturas.size());
    }
}