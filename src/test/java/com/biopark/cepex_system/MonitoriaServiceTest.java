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
import com.biopark.cepex_system.service.MonitoriaService;
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
class MonitoriaServiceTest {

    @Autowired
    private MonitoriaService monitoriaService;
    @Autowired
    private CandidaturaMonitoriaService candidaturaMonitoriaService;
    @Autowired
    private MonitoriaRepository monitoriaRepository;
    @Autowired
    private CandidaturaMonitoriaRepository candidaturaRepository;
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
        // Clear repositories
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
    @DisplayName("Should create a new monitoria successfully")
    void saveMonitoriaSuccess() {
        Monitoria newMonitoria = new Monitoria(
                null,
                "New Monitoria Title",
                "New Description",
                true,
                "Online",
                1,
                10,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusMonths(1),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                SelectionType.ANALISE_HISTORICO,
                LocalDate.now().plusDays(6),
                "10:00",
                LocalDate.now().plusDays(7),
                StatusMonitoria.PENDENTE,
                course,
                discipline,
                professor
        );
        Monitoria savedMonitoria = monitoriaService.save(newMonitoria);
        assertNotNull(savedMonitoria.getId());
        assertEquals("New Monitoria Title", savedMonitoria.getTitle());
    }

    @Test
    @DisplayName("Should find all monitorias")
    void findAllMonitorias() {
        List<Monitoria> monitorias = monitoriaService.findAll(null, null);
        assertFalse(monitorias.isEmpty());
        assertEquals(1, monitorias.size());
    }

    @Test
    @DisplayName("Should find monitoria by ID")
    void findMonitoriaById() {
        Optional<Monitoria> foundMonitoria = monitoriaService.findById(monitoria.getId());
        assertTrue(foundMonitoria.isPresent());
        assertEquals(monitoria.getTitle(), foundMonitoria.get().getTitle());
    }

    @Test
    @DisplayName("Should update an existing monitoria")
    void updateMonitoria() {
        monitoria.setTitle("Updated Monitoria Title");
        Monitoria updatedMonitoria = monitoriaService.save(monitoria);
        assertEquals("Updated Monitoria Title", updatedMonitoria.getTitle());
    }

    @Test
    @DisplayName("Should delete a monitoria by ID")
    void deleteMonitoria() {
        monitoriaService.delete(monitoria.getId());
        Optional<Monitoria> deletedMonitoria = monitoriaService.findById(monitoria.getId());
        assertFalse(deletedMonitoria.isPresent());
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent monitoria")
    void deleteNonExistentMonitoria() {
        UUID nonExistentId = UUID.randomUUID();
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                monitoriaService.delete(nonExistentId)
        );
        assertTrue(thrown.getMessage().contains("Monitoria não encontrada para o ID"));
    }

    @Test
    @DisplayName("Should apply search filter by title")
    void findAllMonitoriasWithSearchByTitle() {
        List<Monitoria> monitorias = monitoriaService.findAll("POO", null);
        assertFalse(monitorias.isEmpty());
        assertEquals(1, monitorias.size());
        assertEquals("Monitoria de POO", monitorias.get(0).getTitle());
    }

    @Test
    @DisplayName("Should apply search filter by professor name")
    void findAllMonitoriasWithSearchByProfessorName() {
        List<Monitoria> monitorias = monitoriaService.findAll("prof test", null);
        assertFalse(monitorias.isEmpty());
        assertEquals(1, monitorias.size());
        assertEquals("Monitoria de POO", monitorias.get(0).getTitle());
    }

    @Test
    @DisplayName("Should apply search filter by subject name")
    void findAllMonitoriasWithSearchBySubjectName() {
        List<Monitoria> monitorias = monitoriaService.findAll("orientada", null);
        assertFalse(monitorias.isEmpty());
        assertEquals(1, monitorias.size());
        assertEquals("Monitoria de POO", monitorias.get(0).getTitle());
    }

    @Test
    @DisplayName("Should filter by status")
    void findAllMonitoriasWithStatusFilter() {
        monitoria.setStatusMonitoria(StatusMonitoria.APROVADA);
        monitoriaService.save(monitoria);
        List<Monitoria> monitorias = monitoriaService.findAll(null, "APROVADA");
        assertFalse(monitorias.isEmpty());
        assertEquals(1, monitorias.size());
        assertEquals(StatusMonitoria.APROVADA, monitorias.get(0).getStatusMonitoria());
    }

    @Test
    @DisplayName("Should return empty list for non-existent search criteria")
    void findAllMonitoriasNoMatch() {
        List<Monitoria> monitorias = monitoriaService.findAll("NonExistent", null);
        assertTrue(monitorias.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for non-existent status")
    void findAllMonitoriasInvalidStatus() {
        List<Monitoria> monitorias = monitoriaService.findAll(null, "INVALID_STATUS");
        // Should return all if status is invalid
        assertFalse(monitorias.isEmpty());
        assertEquals(1, monitorias.size());
    }
}