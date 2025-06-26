package com.biopark.cepex_system.service;

import com.biopark.cepex_system.domain.student.Student;
import com.biopark.cepex_system.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    /**
     * Salva um novo estudante ou atualiza um existente após aplicar as regras de validação.
     * Regras:
     * 1. O RA não pode ser duplicado
     * 2. O CPF não pode ser duplicado
     * 3. O email não pode ser duplicado
     *
     * @param student O objeto Student a ser salvo.
     * @return O objeto Student salvo.
     * @throws IllegalStateException se RA, CPF ou email já existirem.
     */
    @Transactional
    public Student save(Student student) {
        // Validação: Verifica se já existe um estudante com o mesmo RA
        Optional<Student> existingStudentByRa = repository.findByRa(student.getRa());
        if (existingStudentByRa.isPresent() && !existingStudentByRa.get().getId().equals(student.getId())) {
            throw new IllegalStateException("Já existe um estudante cadastrado com o RA: " + student.getRa());
        }

        // Validação: Verifica se já existe um estudante com o mesmo CPF
        Optional<Student> existingStudentByCpf = repository.findByCpf(student.getCpf());
        if (existingStudentByCpf.isPresent() && !existingStudentByCpf.get().getId().equals(student.getId())) {
            throw new IllegalStateException("Já existe um estudante cadastrado com o CPF: " + student.getCpf());
        }

        // Validação: Verifica se já existe um estudante com o mesmo email
        Optional<Student> existingStudentByEmail = repository.findByEmail(student.getEmail());
        if (existingStudentByEmail.isPresent() && !existingStudentByEmail.get().getId().equals(student.getId())) {
            throw new IllegalStateException("Já existe um estudante cadastrado com o email: " + student.getEmail());
        }

        // Se passar em todas as validações, salva no banco de dados
        return repository.save(student);
    }

    /**
     * Busca todos os estudantes.
     * @return Lista de todos os estudantes.
     */
    public List<Student> findAll() {
        return repository.findAll();
    }

    /**
     * Busca um estudante pelo ID.
     * @param id O ID do estudante.
     * @return Optional contendo o estudante se encontrado.
     */
    public Optional<Student> findById(UUID id) {
        return repository.findById(id);
    }

    /**
     * Busca um estudante pelo RA.
     * @param ra O RA do estudante.
     * @return Optional contendo o estudante se encontrado.
     */
    public Optional<Student> findByRa(String ra) {
        return repository.findByRa(ra);
    }

    /**
     * Busca um estudante pelo CPF.
     * @param cpf O CPF do estudante.
     * @return Optional contendo o estudante se encontrado.
     */
    public Optional<Student> findByCpf(String cpf) {
        return repository.findByCpf(cpf);
    }

    /**
     * Busca um estudante pelo email.
     * @param email O email do estudante.
     * @return Optional contendo o estudante se encontrado.
     */
    public Optional<Student> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * Deleta um estudante pelo ID.
     * @param id O ID do estudante a ser deletado.
     * @throws EntityNotFoundException se o estudante não for encontrado.
     */
    @Transactional
    public void delete(UUID id) {
        Student studentToDelete = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado para o ID: " + id));
        repository.delete(studentToDelete);
    }

    /**
     * Atualiza o status de um estudante.
     * @param id O ID do estudante.
     * @param active O novo status.
     * @return Optional contendo o estudante atualizado.
     */
    @Transactional
    public Optional<Student> updateStatus(UUID id, boolean active) {
        return repository.findById(id)
                .map(student -> {
                    student.setStatus(active);
                    return repository.save(student);
                });
    }
} 