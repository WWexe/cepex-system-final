package com.biopark.cepex_system.service;

import com.biopark.cepex_system.domain.professor.Professor;
import com.biopark.cepex_system.repository.ProfessorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfessorService {

    private final ProfessorRepository repository;

    public ProfessorService(ProfessorRepository repository) {
        this.repository = repository;
    }

    /**
     * Salva um novo professor ou atualiza um existente após aplicar as regras de validação.
     * Regras:
     * 1. O RA não pode ser duplicado
     * 2. O CPF não pode ser duplicado
     * 3. O email não pode ser duplicado
     *
     * @param professor O objeto Professor a ser salvo.
     * @return O objeto Professor salvo.
     * @throws IllegalStateException se RA, CPF ou email já existirem.
     */
    @Transactional
    public Professor save(Professor professor) {
        // Validação: Verifica se já existe um professor com o mesmo RA
        Optional<Professor> existingProfessorByRa = repository.findByRa(professor.getRa());
        if (existingProfessorByRa.isPresent() && !existingProfessorByRa.get().getId().equals(professor.getId())) {
            throw new IllegalStateException("Já existe um professor cadastrado com o RA: " + professor.getRa());
        }

        // Validação: Verifica se já existe um professor com o mesmo CPF
        Optional<Professor> existingProfessorByCpf = repository.findByCpf(professor.getCpf());
        if (existingProfessorByCpf.isPresent() && !existingProfessorByCpf.get().getId().equals(professor.getId())) {
            throw new IllegalStateException("Já existe um professor cadastrado com o CPF: " + professor.getCpf());
        }

        // Validação: Verifica se já existe um professor com o mesmo email
        Optional<Professor> existingProfessorByEmail = repository.findByEmail(professor.getEmail());
        if (existingProfessorByEmail.isPresent() && !existingProfessorByEmail.get().getId().equals(professor.getId())) {
            throw new IllegalStateException("Já existe um professor cadastrado com o email: " + professor.getEmail());
        }

        // Se passar em todas as validações, salva no banco de dados
        return repository.save(professor);
    }

    public List<Professor> findAll() {
        return repository.findAll();
    }

    public Optional<Professor> findById(UUID id) {
        return repository.findById(id);
    }

    /**
     * Busca um professor pelo RA.
     * @param ra O RA do professor.
     * @return Optional contendo o professor se encontrado.
     */
    public Optional<Professor> findByRa(String ra) {
        return repository.findByRa(ra);
    }

    /**
     * Busca um professor pelo CPF.
     * @param cpf O CPF do professor.
     * @return Optional contendo o professor se encontrado.
     */
    public Optional<Professor> findByCpf(String cpf) {
        return repository.findByCpf(cpf);
    }

    /**
     * Busca um professor pelo email.
     * @param email O email do professor.
     * @return Optional contendo o professor se encontrado.
     */
    public Optional<Professor> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Transactional
    public void delete(UUID id) {
        Professor professorToDelete = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado para o ID: " + id));
        repository.delete(professorToDelete);
    }

    /**
     * Atualiza o status de um professor.
     * @param id O ID do professor.
     * @param active O novo status.
     * @return Optional contendo o professor atualizado.
     */
    @Transactional
    public Optional<Professor> updateStatus(UUID id, boolean active) {
        return repository.findById(id)
                .map(professor -> {
                    professor.setActive(active);
                    return repository.save(professor);
                });
    }
}