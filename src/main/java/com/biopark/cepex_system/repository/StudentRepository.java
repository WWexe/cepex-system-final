package com.biopark.cepex_system.repository;

import com.biopark.cepex_system.domain.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    
    /**
     * Busca um estudante pelo RA.
     * @param ra O RA do estudante.
     * @return um Optional contendo o estudante se encontrado, ou um Optional vazio caso contrário.
     */
    Optional<Student> findByRa(String ra);
    
    /**
     * Busca um estudante pelo CPF.
     * @param cpf O CPF do estudante.
     * @return um Optional contendo o estudante se encontrado, ou um Optional vazio caso contrário.
     */
    Optional<Student> findByCpf(String cpf);
    
    /**
     * Busca um estudante pelo email.
     * @param email O email do estudante.
     * @return um Optional contendo o estudante se encontrado, ou um Optional vazio caso contrário.
     */
    Optional<Student> findByEmail(String email);
    
    /**
     * Verifica se existe um estudante com o RA fornecido.
     * @param ra O RA a ser verificado.
     * @return true se existir, false caso contrário.
     */
    boolean existsByRa(String ra);
    
    /**
     * Verifica se existe um estudante com o CPF fornecido.
     * @param cpf O CPF a ser verificado.
     * @return true se existir, false caso contrário.
     */
    boolean existsByCpf(String cpf);
    
    /**
     * Verifica se existe um estudante com o email fornecido.
     * @param email O email a ser verificado.
     * @return true se existir, false caso contrário.
     */
    boolean existsByEmail(String email);
} 