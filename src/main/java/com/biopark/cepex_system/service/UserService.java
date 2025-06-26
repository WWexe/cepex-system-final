package com.biopark.cepex_system.service;

// Imports originais e novos
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.domain.user.UserRole;
import com.biopark.cepex_system.domain.usermanager.UserDTO;
import com.biopark.cepex_system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    // NOVA DEPENDÊNCIA: Adicionada para as novas funcionalidades.
    private final PasswordEncoder passwordEncoder;

    /**
     * CONSTRUTOR ATUALIZADO: Unifica as dependências do código antigo e do novo.
     * Esta alteração é necessária para que os novos métodos que dependem do PasswordEncoder funcionem.
     */
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // ===================================================================
    // MÉTODOS ORIGINAIS DE UserService.java (INTOCADOS)
    // ===================================================================

    @Transactional
    public User save(User user) {
        return repository.save(user);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public void delete(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Usuário não encontrado para o ID: " + id);
        }
    }

    public UserRoleCountsDTO getUserRoleCounts() {
        long studentCount = repository.countByRole(UserRole.STUDENT);
        long professorCount = repository.countByRole(UserRole.PROFESSOR);
        long coordenationCount = repository.countByRole(UserRole.COORDENATION);
        long secretaryCount = repository.countByRole(UserRole.SECRETARY);

        return new UserRoleCountsDTO(studentCount, professorCount, coordenationCount, secretaryCount);
    }

    // Atualizar o status de um usuário
    @Transactional
    public Optional<User> updateStatus(UUID id, boolean newStatus) {
        return repository.findById(id)
                .map(user -> {
                    user.setStatus(newStatus);
                    return repository.save(user);
                });
    }

    // Atualizar o papel (role) de um usuário
    @Transactional
    public Optional<User> updateRole(UUID id, UserRole newRole) {
        return repository.findById(id)
                .map(user -> {
                    user.setRole(newRole);
                    return repository.save(user);
                });
    }

    public record UserRoleCountsDTO(long studentCount, long professorCount, long coordenationCount, long secretaryCount) {}

    // ===================================================================
    // NOVOS MÉTODOS DE UserService(2).java (ADICIONADOS)
    // ===================================================================

    /**
     * Cria um novo usuário a partir de um DTO, incluindo a criptografia da senha.
     * @param dto O DTO com os dados do novo usuário.
     * @return A entidade User salva.
     */
    @Transactional
    public User createNewUser(UserDTO dto) {
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());
        UserRole role = "Aluno".equalsIgnoreCase(dto.getTipo()) ? UserRole.STUDENT : UserRole.PROFESSOR;

        // Supondo que a entidade User tenha um construtor que aceite login, email, senha e role.
        User newUser = new User(dto.getLogin(), dto.getEmail(), encryptedPassword, role);

        return repository.save(newUser);
    }

    /**
     * Atualiza um usuário existente a partir de um DTO.
     * @param id o UUID do usuário.
     * @param dto o DTO com os novos dados.
     * @return a entidade User atualizada.
     */
    @Transactional
    public User updateUser(UUID id, UserDTO dto) {
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));

        existingUser.setEmail(dto.getEmail());
        existingUser.setLogin(dto.getLogin());

        if ("Aluno".equalsIgnoreCase(dto.getTipo())) {
            existingUser.setRole(UserRole.STUDENT);
        } else if ("Professor".equalsIgnoreCase(dto.getTipo())) {
            existingUser.setRole(UserRole.PROFESSOR);
        }

        return repository.save(existingUser);
    }

    /**
     * Encontra todos os usuários de forma paginada.
     * @param page o número da página (começando em 0).
     * @param size o tamanho da página.
     * @return uma Página de usuários.
     */
    public Page<User> findAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }
}
