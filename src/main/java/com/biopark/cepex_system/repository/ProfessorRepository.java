package com.biopark.cepex_system.repository;

import com.biopark.cepex_system.domain.professor.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {
    
    /**
     * Busca um professor pelo RA.
     * @param ra O RA do professor.
     * @return um Optional contendo o professor se encontrado, ou um Optional vazio caso contrário.
     */
    Optional<Professor> findByRa(String ra);
    
    /**
     * Busca um professor pelo CPF.
     * @param cpf O CPF do professor.
     * @return um Optional contendo o professor se encontrado, ou um Optional vazio caso contrário.
     */
    Optional<Professor> findByCpf(String cpf);
    
    /**
     * Busca um professor pelo email.
     * @param email O email do professor.
     * @return um Optional contendo o professor se encontrado, ou um Optional vazio caso contrário.
     */
    Optional<Professor> findByEmail(String email);
    
    /**
     * Verifica se existe um professor com o RA fornecido.
     * @param ra O RA a ser verificado.
     * @return true se existir, false caso contrário.
     */
    boolean existsByRa(String ra);
    
    /**
     * Verifica se existe um professor com o CPF fornecido.
     * @param cpf O CPF a ser verificado.
     * @return true se existir, false caso contrário.
     */
    boolean existsByCpf(String cpf);
    
    /**
     * Verifica se existe um professor com o email fornecido.
     * @param email O email a ser verificado.
     * @return true se existir, false caso contrário.
     */
    boolean existsByEmail(String email);
}
