package com.biopark.cepex_system;

import com.biopark.cepex_system.domain.course.Course;
import com.biopark.cepex_system.domain.student.Student;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.domain.user.UserRole;
import com.biopark.cepex_system.repository.CourseRepository;
import com.biopark.cepex_system.repository.StudentRepository;
import com.biopark.cepex_system.repository.UserRepository;
import com.biopark.cepex_system.service.StudentService;
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
class StudentServiceTest {

    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;

    private User user1;
    private User user2;
    private Course course;
    private Student student1;

    @BeforeEach
    void setUp() {
        // Clear repositories
        studentRepository.deleteAll();
        userRepository.deleteAll();
        courseRepository.deleteAll();

        // Setup common test data
        user1 = new User(UUID.randomUUID(), "studentuser1", "hashed_password", "student1@example.com", true, UserRole.STUDENT);
        user2 = new User(UUID.randomUUID(), "studentuser2", "hashed_password", "student2@example.com", true, UserRole.STUDENT);
        userRepository.saveAll(List.of(user1, user2));

        course = new Course(UUID.randomUUID(), "Engenharia de Software", 8, true, LocalDateTime.now());
        courseRepository.save(course);

        student1 = new Student(UUID.randomUUID(), "Pedro", "Oliveira", "pedro.oliveria@example.com", "9999-1111", "RA001", "11122233344", true, LocalDateTime.now(), user1, course);
        studentRepository.save(student1);
    }

    @Test
    @DisplayName("Should save a new student successfully")
    void saveStudentSuccess() {
        Student newStudent = new Student(UUID.randomUUID(), "Ana", "Souza", "ana.souza@example.com", "9999-2222", "RA002", "55566677788", true, LocalDateTime.now(), user2, course);
        Student savedStudent = studentService.save(newStudent);
        assertNotNull(savedStudent.getId());
        assertEquals("Ana", savedStudent.getFirstName());
    }

    @Test
    @DisplayName("Should throw exception if RA already exists when creating a new student")
    void saveStudentDuplicateRa() {
        Student newStudentWithDuplicateRa = new Student(UUID.randomUUID(), "Carlos", "Lima", "carlos.lima@example.com", "9999-3333", "RA001", "99988877766", true, LocalDateTime.now(), user2, course);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                studentService.save(newStudentWithDuplicateRa)
        );
        assertTrue(exception.getMessage().contains("Já existe um estudante cadastrado com o RA: RA001"));
    }

    @Test
    @DisplayName("Should throw exception if CPF already exists when creating a new student")
    void saveStudentDuplicateCpf() {
        Student newStudentWithDuplicateCpf = new Student(UUID.randomUUID(), "Bruna", "Costa", "bruna.costa@example.com", "9999-4444", "RA003", "11122233344", true, LocalDateTime.now(), user2, course);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                studentService.save(newStudentWithDuplicateCpf)
        );
        assertTrue(exception.getMessage().contains("Já existe um estudante cadastrado com o CPF: 11122233344"));
    }

    @Test
    @DisplayName("Should throw exception if Email already exists when creating a new student")
    void saveStudentDuplicateEmail() {
        Student newStudentWithDuplicateEmail = new Student(UUID.randomUUID(), "Daniel", "Alves", "pedro.oliveria@example.com", "9999-5555", "RA004", "12312312312", true, LocalDateTime.now(), user2, course);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                studentService.save(newStudentWithDuplicateEmail)
        );
        assertTrue(exception.getMessage().contains("Já existe um estudante cadastrado com o email: pedro.oliveria@example.com"));
    }

    @Test
    @DisplayName("Should allow updating a student without changing unique fields")
    void updateStudentSuccess() {
        student1.setFirstName("Pedro Atualizado");
        Student updatedStudent = studentService.save(student1);
        assertEquals("Pedro Atualizado", updatedStudent.getFirstName());
    }

    @Test
    @DisplayName("Should not allow updating a student to a duplicate RA of another student")
    void updateStudentToDuplicateRa() {
        Student student2 = new Student(UUID.randomUUID(), "Outro", "Estudante", "outro.estudante@example.com", "1234-5678", "RA_DUPLICADO", "98765432109", true, LocalDateTime.now(), user2, course);
        studentRepository.save(student2);

        student1.setRa("RA_DUPLICADO"); // Tenta usar o RA do student2
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                studentService.save(student1)
        );
        assertTrue(exception.getMessage().contains("Já existe um estudante cadastrado com o RA: RA_DUPLICADO"));
    }
}