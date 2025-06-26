package com.biopark.cepex_system;

import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.domain.user.UserRole;
import com.biopark.cepex_system.infra.security.TokenService;
import com.biopark.cepex_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthorizationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;

    private String adminToken;
    private String studentToken;
    private String professorToken;
    private String coordenationToken;
    private String secretaryToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // Clean up before each test

        // Create and save users with different roles
        User adminUser = new User("admin", "admin@biopark.edu.br", passwordEncoder.encode("admin123"), UserRole.ADMIN);
        adminUser.setStatus(true);
        userRepository.save(adminUser);
        adminToken = tokenService.generateToken(adminUser);

        User studentUser = new User("estudante1", "estudante1@biopark.edu.br", passwordEncoder.encode("admin123"), UserRole.STUDENT);
        studentUser.setStatus(true);
        userRepository.save(studentUser);
        studentToken = tokenService.generateToken(studentUser);

        User professorUser = new User("professor1", "professor1@biopark.edu.br", passwordEncoder.encode("admin123"), UserRole.PROFESSOR);
        professorUser.setStatus(true);
        userRepository.save(professorUser);
        professorToken = tokenService.generateToken(professorUser);

        User coordenationUser = new User("coordenador", "coordenador@biopark.edu.br", passwordEncoder.encode("admin123"), UserRole.COORDENATION);
        coordenationUser.setStatus(true);
        userRepository.save(coordenationUser);
        coordenationToken = tokenService.generateToken(coordenationUser);

        User secretaryUser = new User("secretaria", "secretaria@biopark.edu.br", passwordEncoder.encode("admin123"), UserRole.SECRETARY);
        secretaryUser.setStatus(true);
        userRepository.save(secretaryUser);
        secretaryToken = tokenService.generateToken(secretaryUser);
    }

    @Test
    @DisplayName("Admin should access /users/**")
    void adminAccessUsers() throws Exception {
        mockMvc.perform(get("/users/")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Student should not access /users/**")
    void studentCannotAccessUsers() throws Exception {
        mockMvc.perform(get("/users/")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Professor should access /monitorias/statistics")
    void professorAccessMonitoriaStatistics() throws Exception {
        mockMvc.perform(get("/monitorias/statistics")
                        .header("Authorization", "Bearer " + professorToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Student should not access /monitorias/statistics")
    void studentCannotAccessMonitoriaStatistics() throws Exception {
        mockMvc.perform(get("/monitorias/statistics")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Professor should create /monitorias")
    void professorCanCreateMonitoria() throws Exception {
        // Minimal valid JSON for Monitoria creation (UUIDs for FKs can be mocked or pre-created)
        // For a real test, you would need to create a valid Course, Discipline, and Professor first.
        // This is a simplified check for authorization, assuming valid data.
        mockMvc.perform(post("/monitorias")
                        .header("Authorization", "Bearer " + professorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Monitoria\", \"description\":\"Desc\", \"remote\":false, \"location\":\"Lab\", \"vacancies\":1, \"workload\":10, \"inicialDate\":\"2025-01-01\", \"finalDate\":\"2025-06-30\", \"inicialIngressDate\":\"2024-12-01\", \"finalIngressDate\":\"2024-12-15\", \"selectionType\":\"ENTREVISTA\", \"statusMonitoria\":\"PENDENTE\", \"course\": {\"id\": \"" + UUID.randomUUID() + "\"}, \"subject\": {\"id\": \"" + UUID.randomUUID() + "\"}, \"professor\": {\"id\": \"" + UUID.randomUUID() + "\"}}"))
                .andExpect(status().isOk()); // Or 201 Created depending on your controller
    }

    @Test
    @DisplayName("Student should not create /monitorias")
    void studentCannotCreateMonitoria() throws Exception {
        mockMvc.perform(post("/monitorias")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Invalid content, but tests authorization
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Student should access /monitorias/{monitoriaId}/candidatura-status")
    void studentCanCheckCandidaturaStatus() throws Exception {
        // A real monitoria ID would be needed here
        mockMvc.perform(get("/monitorias/" + UUID.randomUUID() + "/candidatura-status")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // It expects 200 OK because the service handles non-existent monitoria by returning false
    }

    @Test
    @DisplayName("Unauthorized access to protected endpoint should return 403")
    void unauthorizedAccess() throws Exception {
        mockMvc.perform(get("/users/")
                        .header("Authorization", "Bearer INVALID_TOKEN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // Or 401 Unauthorized, depending on filter chain. 403 is more specific after token validation failure if the user is then considered anonymous.
    }
}