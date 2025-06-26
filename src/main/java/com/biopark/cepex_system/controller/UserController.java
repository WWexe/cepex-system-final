package com.biopark.cepex_system.controller;

import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.domain.user.UserRole;
import com.biopark.cepex_system.service.UserService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(
        summary = "Listar todos os usuários",
        description = "Retorna uma lista de todos os usuários cadastrados no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuários retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acesso negado - requer permissões de administrador"
        )
    })
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar usuário por ID",
        description = "Retorna um usuário específico pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuário encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado"
        )
    })
    public ResponseEntity<User> getUserById(
        @Parameter(
            description = "ID do usuário",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440001"
        )
        @PathVariable UUID id
    ) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
        summary = "Criar novo usuário",
        description = "Cria um novo usuário no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuário criado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos"
        )
    })
    public ResponseEntity<User> createUser(
        @Parameter(
            description = "Dados do usuário",
            required = true
        )
        @RequestBody @Valid User user
    ) {
        User savedUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar usuário",
        description = "Atualiza os dados de um usuário existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuário atualizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado"
        )
    })
    public ResponseEntity<User> updateUser(
        @Parameter(
            description = "ID do usuário",
            required = true
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Dados atualizados do usuário",
            required = true
        )
        @RequestBody @Valid User user
    ) {
        Optional<User> existingUser = userService.findById(id);
        if (existingUser.isPresent()) {
            user.setId(id);
            User updatedUser = userService.save(user);
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar usuário",
        description = "Remove um usuário do sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Usuário deletado com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado"
        )
    })
    public ResponseEntity<Void> deleteUser(
        @Parameter(
            description = "ID do usuário",
            required = true
        )
        @PathVariable UUID id
    ) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats")
    @Operation(
        summary = "Estatísticas de usuários",
        description = "Retorna estatísticas sobre os usuários por role"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estatísticas retornadas com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserService.UserRoleCountsDTO.class),
                examples = @ExampleObject(
                    value = "{\"studentCount\": 150, \"professorCount\": 25, \"coordenationCount\": 5, \"secretaryCount\": 3}"
                )
            )
        )
    })
    public ResponseEntity<UserService.UserRoleCountsDTO> getUserStats() {
        UserService.UserRoleCountsDTO stats = userService.getUserRoleCounts();
        return ResponseEntity.ok(stats);
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Atualizar status do usuário",
        description = "Ativa ou desativa um usuário"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Status atualizado com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado"
        )
    })
    public ResponseEntity<?> updateUserStatus(
        @Parameter(
            description = "ID do usuário",
            required = true
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Novo status (true = ativo, false = inativo)",
            required = true
        )
        @RequestParam boolean status
    ) {
        Optional<User> updatedUser = userService.updateStatus(id, status);
        return updatedUser.map(user -> ResponseEntity.ok("Status atualizado com sucesso"))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/role")
    @Operation(
        summary = "Atualizar role do usuário",
        description = "Altera o papel/perfil do usuário no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Role atualizado com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado"
        )
    })
    public ResponseEntity<?> updateUserRole(
        @Parameter(
            description = "ID do usuário",
            required = true
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Novo role do usuário",
            required = true,
            schema = @Schema(implementation = UserRole.class)
        )
        @RequestParam UserRole role
    ) {
        Optional<User> updatedUser = userService.updateRole(id, role);
        return updatedUser.map(user -> ResponseEntity.ok("Role atualizado com sucesso"))
                .orElse(ResponseEntity.notFound().build());
    }
}