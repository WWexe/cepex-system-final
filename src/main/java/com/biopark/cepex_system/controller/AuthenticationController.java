package com.biopark.cepex_system.controller;

import com.biopark.cepex_system.domain.user.*;
import com.biopark.cepex_system.infra.security.TokenService;
import com.biopark.cepex_system.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar PasswordEncoder
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e gerenciamento de usuários")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenService tokenService;
    @Autowired // Injetar o PasswordEncoder
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(
        summary = "Realizar login",
        description = "Autentica um usuário e retorna um token JWT válido"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponseDTO.class),
                examples = @ExampleObject(
                    value = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciais inválidas",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                    value = "Falha na autenticação: Login ou senha inválidos."
                )
            )
        )
    })
    public ResponseEntity<?> login(
        @Parameter(
            description = "Credenciais do usuário",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    value = "{\"login\": \"admin\", \"password\": \"admin123\"}"
                )
            )
        )
        @RequestBody @Valid AuthenticationDTO data
    ) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((User) auth.getPrincipal());

            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (AuthenticationException e) {
            // Logar a exceção e/ou retornar uma mensagem de erro mais genérica
            // e.g., "Invalid username or password"
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Falha na autenticação: Login ou senha inválidos.");
        }
    }

    @PostMapping("/register")
    @Operation(
        summary = "Registrar novo usuário",
        description = "Cria um novo usuário no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuário registrado com sucesso",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                    value = "Usuário registrado com sucesso!"
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Login ou email já cadastrado",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                    value = "Erro: Login já cadastrado."
                )
            )
        )
    })
    public ResponseEntity<?> register(
        @Parameter(
            description = "Dados do novo usuário",
            required = true
        )
        @RequestBody @Valid RegisterDTO data
    ) {
        // Verifica se o login já existe
        if(this.repository.findByLogin(data.login()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Login já cadastrado.");
        }
        // Opcional: Verificar se o email já existe, se for um requisito
        if(this.repository.findByEmail(data.email()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: Email já cadastrado.");
        }

        // Criptografa a senha antes de salvar
        String encryptedPassword = passwordEncoder.encode(data.password());
        // Cria o novo usuário usando o construtor que definimos em User.java
        User newUser = new User(data.login(), data.email(), encryptedPassword, data.role());

        this.repository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso!");
    }

    @PostMapping("/forgot-password")
    @Operation(
        summary = "Solicitar recuperação de senha",
        description = "Inicia o processo de recuperação de senha via email"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Solicitação processada",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                    value = "Se o email estiver cadastrado, você receberá as instruções de recuperação."
                )
            )
        )
    })
    public ResponseEntity<?> forgotPassword(
        @Parameter(
            description = "Email do usuário",
            required = true
        )
        @RequestBody @Valid ForgotPasswordDTO data
    ) {
        User user = repository.findByEmail(data.email());
        
        if (user == null) {
            // Por segurança, não informamos se o email existe ou não
            return ResponseEntity.ok().body("Se o email estiver cadastrado, você receberá as instruções de recuperação.");
        }

        // Gerar um token único para recuperação de senha
        String resetToken = UUID.randomUUID().toString();
        
        // TODO: Implementar o envio de email com o token
        // Por enquanto, apenas retornamos uma mensagem de sucesso
        return ResponseEntity.ok().body("Se o email estiver cadastrado, você receberá as instruções de recuperação.");
    }

    @PostMapping("/reset-password")
    @Operation(
        summary = "Redefinir senha",
        description = "Redefine a senha do usuário usando o token de recuperação"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Senha redefinida com sucesso",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                    value = "Senha atualizada com sucesso!"
                )
            )
        )
    })
    public ResponseEntity<?> resetPassword(
        @Parameter(
            description = "Dados para redefinição de senha",
            required = true
        )
        @RequestBody @Valid ResetPasswordDTO data
    ) {
        // TODO: Implementar a validação do token e atualização da senha
        // Por enquanto, apenas retornamos uma mensagem de sucesso
        return ResponseEntity.ok().body("Senha atualizada com sucesso!");
    }
}