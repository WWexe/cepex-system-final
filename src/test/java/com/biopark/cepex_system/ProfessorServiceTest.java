package com.biopark.cepex_system;

import com.biopark.cepex_system.service.ProfessorService;
import com.biopark.cepex_system.domain.professor.Professor;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.domain.user.UserRole;
import com.biopark.cepex_system.repository.ProfessorRepository;
import com.biopark.cepex_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProfessorServiceTest {

    @Autowired
    private ProfessorService professorService;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private Professor professor1;

    @BeforeEach
    void setUp() {
        // Clear repositories
        professorRepository.deleteAll();
        userRepository.deleteAll();

        // Setup common test users
        user1 = new User(UUID.randomUUID(), "profuser1", "hashed_password", "prof1@example.com", true, UserRole.PROFESSOR);
        user2 = new User(UUID.randomUUID(), "profuser2", "hashed_password", "prof2@example.com", true, UserRole.PROFESSOR);
        userRepository.saveAll(List.of(user1, user2));

        professor1 = new Professor(UUID.randomUUID(), "Joao", "Silva", "joao.silva@example.com", "RA123", "11122233344", "9999-8888", true, LocalDateTime.now(), user1, null);
        professorRepository.save(professor1);
    }

    @Test
    @DisplayName("Should save a new professor successfully")
    void saveProfessorSuccess() {
        Professor newProfessor = new Professor(UUID.randomUUID(), "Maria", "Souza", "maria.souza@example.com", "RA456", "55566677788", "1111-2222", true, LocalDateTime.now(), user2, null);
        Professor savedProfessor = professorService.save(newProfessor);
        assertNotNull(savedProfessor.getId());
        assertEquals("Maria", savedProfessor.getFirstName());
    }

    @Test
    @DisplayName("Should throw exception if RA already exists when creating a new professor")
    void saveProfessorDuplicateRa() {
        Professor newProfessorWithDuplicateRa = new Professor(UUID.randomUUID(), "Pedro", "Lima", "pedro.lima@example.com", "RA123", "99988877766", "3333-4444", true, LocalDateTime.now(), user2, null);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                professorService.save(newProfessorWithDuplicateRa)
        );
        assertTrue(exception.getMessage().contains("Já existe um professor cadastrado com o RA: RA123"));
    }

    @Test
    @DisplayName("Should throw exception if CPF already exists when creating a new professor")
    void saveProfessorDuplicateCpf() {
        Professor newProfessorWithDuplicateCpf = new Professor(UUID.randomUUID(), "Ana", "Carla", "ana.carla@example.com", "RA789", "11122233344", "5555-6666", true, LocalDateTime.now(), user2, null);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                professorService.save(newProfessorWithDuplicateCpf)
        );
        assertTrue(exception.getMessage().contains("Já existe um professor cadastrado com o CPF: 11122233344"));
    }

    @Test
    @DisplayName("Should throw exception if Email already exists when creating a new professor")
    void saveProfessorDuplicateEmail() {
        Professor newProfessorWithDuplicateEmail = new Professor(UUID.randomUUID(), "Carlos", "Santos", "joao.silva@example.com", "RA000", "12312312312", "7777-8888", true, LocalDateTime.now(), user2, null);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                professorService.save(newProfessorWithDuplicateEmail)
        );
        assertTrue(exception.getMessage().contains("Já existe um professor cadastrado com o email: joao.silva@example.com"));
    }

    @Test
    @DisplayName("Should allow updating a professor without changing unique fields")
    void updateProfessorSuccess() {
        professor1.setFirstName("João Atualizado");
        Professor updatedProfessor = professorService.save(professor1);
        assertEquals("João Atualizado", updatedProfessor.getFirstName());
    }

    @Test
    @DisplayName("Should allow updating a professor and changing unique fields to new unique values")
    void updateProfessorChangeUniqueFields() {
        Professor professorToUpdate = new Professor(UUID.randomUUID(), "Teste", "Atualizacao", "teste.atualizacao@example.com", "RA555", "12345678900", "0000-0000", true, LocalDateTime.now(), user1, null);
        professorRepository.save(professorToUpdate);

        professorToUpdate.setRa("RA_NEW");
        professorToUpdate.setCpf("00011122233");
        professorToUpdate.setEmail("new.email@example.com");

        Professor updatedProfessor = professorService.save(professorToUpdate);
        assertEquals("RA_NEW", updatedProfessor.getRa());
        assertEquals("00011122233", updatedProfessor.getCpf());
        assertEquals("new.email@example.com", updatedProfessor.getEmail());
    }

    @Test
    @DisplayName("Should not allow updating a professor to a duplicate RA of another professor")
    void updateProfessorToDuplicateRa() {
        Professor professor2 = new Professor(UUID.randomUUID(), "Outro", "Professor", "outro.prof@example.com", "RA_DUPLICADO", "98765432109", "1234-5678", true, LocalDateTime.now(), user2, null);
        professorRepository.save(professor2);

        professor1.setRa("RA_DUPLICADO"); // Tenta usar o RA do professor2
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                professorService.save(professor1)
        );
        assertTrue(exception.getMessage().contains("Já existe um professor cadastrado com o RA: RA_DUPLICADO"));
    }
}