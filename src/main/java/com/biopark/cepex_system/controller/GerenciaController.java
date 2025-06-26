/**
 * Este é o controller unificado que substitui o GerenciaController.
 * Ele serve como o ponto de entrada da API REST para todas as operações de usuário,
 * comunicando-se exclusivamente com o novo UserService e usando o UserDTO correto.
 */
package com.biopark.cepex_system.controller;

import com.biopark.cepex_system.domain.usermanager.UserDTO ;
import com.biopark.cepex_system.domain.user.User ;
import com.biopark.cepex_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class GerenciaController {

    private final UserService userService;

    @Autowired
    public GerenciaController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Lista usuários de forma paginada.
     * Chama o método correto do serviço e converte o resultado para DTO.
     */
    @GetMapping
    public ResponseEntity<Page<UserDTO>> listarPaginado(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<User> userPage = userService.findAllPaginated(page, size);
        Page<UserDTO> userDTOPage = userPage.map(this::toDTO);
        return ResponseEntity.ok(userDTOPage);
    }

    /**
     * Cria um novo usuário.
     * Passa o DTO diretamente para o serviço, que contém a lógica de criação.
     */
    @PostMapping
    public ResponseEntity<UserDTO> salvarUsuario(@RequestBody UserDTO userDTO) {
        // CORRIGIDO: chama o método 'createNewUser' que espera um DTO.
        User savedUser = userService.createNewUser(userDTO);
        return new ResponseEntity<>(toDTO(savedUser), HttpStatus.CREATED);
    }

    /**
     * Atualiza um usuário existente.
     * O ID agora é do tipo UUID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> atualizarUsuario(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        // CORRIGIDO: chama o método 'updateUser' que espera um DTO.
        User updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(toDTO(updatedUser));
    }

    /**
     * Exclui um usuário.
     * O ID agora é do tipo UUID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Métodos de Conversão (Helpers) ---

    /**
     * Converte uma entidade User para um UserDTO para ser enviado ao front-end.
     * Utiliza apenas os campos que existem na entidade User.
     */
    private UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        // CORRIGIDO: A entidade User não tem um campo 'name', então usamos o 'login' como nome de exibição.
        dto.setName(user.getLogin());
        dto.setLogin(user.getLogin());
        dto.setEmail(user.getEmail());
        // Os campos cpf e ra não existem na entidade User, então não são mapeados aqui.
        dto.setRole(user.getRole());
        dto.setStatus(user.isEnabled());
        return dto;
    }

    // O método toEntity foi removido pois a lógica de criação agora está no UserService,
    // o que torna o controller mais limpo e focado em suas responsabilidades de API.
}
