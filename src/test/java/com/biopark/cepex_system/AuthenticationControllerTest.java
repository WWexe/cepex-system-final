package com.biopark.cepex_system;

import com.biopark.cepex_system.domain.user.*;
import com.biopark.cepex_system.infra.security.TokenService;
import com.biopark.cepex_system.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // Clean up before each test
    }

    @Test
    @DisplayName("Should authenticate user and return token successfully")
    void loginSuccess() throws Exception {
        String encryptedPassword = passwordEncoder.encode("testpassword");
        User testUser = new User("testuser", "test@example.com", encryptedPassword, UserRole.STUDENT);
        userRepository.save(testUser);

        AuthenticationDTO authDto = new AuthenticationDTO("testuser", "testpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("Should return 401 for invalid login credentials")
    void loginInvalidCredentials() throws Exception {
        // No user registered
        AuthenticationDTO authDto = new AuthenticationDTO("nonexistent", "wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should register a new user successfully")
    void registerSuccess() throws Exception {
        RegisterDTO registerDto = new RegisterDTO("newuser", "newuser@example.com", "securepassword", UserRole.STUDENT);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value("Usuário registrado com sucesso!"));

        User registeredUser = (User) userRepository.findByLogin("newuser");
        assertNotNull(registeredUser);
        assertTrue(passwordEncoder.matches("securepassword", registeredUser.getPassword()));
        assertEquals(UserRole.STUDENT, registeredUser.getRole());
    }

    @Test
    @DisplayName("Should return 409 if login already exists during registration")
    void registerDuplicateLogin() throws Exception {
        String encryptedPassword = passwordEncoder.encode("existingpassword");
        User existingUser = new User("existinguser", "existing@example.com", encryptedPassword, UserRole.STUDENT);
        userRepository.save(existingUser);

        RegisterDTO registerDto = new RegisterDTO("existinguser", "another@example.com", "password", UserRole.STUDENT);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value("Erro: Login já cadastrado."));
    }

    @Test
    @DisplayName("Should return 409 if email already exists during registration")
    void registerDuplicateEmail() throws Exception {
        String encryptedPassword = passwordEncoder.encode("existingpassword");
        User existingUser = new User("user1", "duplicate@example.com", encryptedPassword, UserRole.STUDENT);
        userRepository.save(existingUser);

        RegisterDTO registerDto = new RegisterDTO("user2", "duplicate@example.com", "password", UserRole.STUDENT);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value("Erro: Email já cadastrado."));
    }

    // Testes para /forgot-password e /reset-password (apenas validação de requisição, não a lógica de envio de email)
    @Test
    @DisplayName("Should return success message for forgot-password request even if email does not exist")
    void forgotPasswordRequest() throws Exception {
        ForgotPasswordDTO forgotPasswordDto = new ForgotPasswordDTO("nonexistent@example.com");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotPasswordDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Se o email estiver cadastrado, você receberá as instruções de recuperação."));
    }

    @Test
    @DisplayName("Should return success message for reset-password request")
    void resetPasswordRequest() throws Exception {
        ResetPasswordDTO resetPasswordDto = new ResetPasswordDTO("valid-token-uuid", "newPassword123", "newPassword123");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetPasswordDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Senha atualizada com sucesso!"));
    }
}