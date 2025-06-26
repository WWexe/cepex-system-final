package com.biopark.cepex_system.service;

import com.biopark.cepex_system.domain.course.Discipline;
import com.biopark.cepex_system.repository.DisciplineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DisciplineService {

    private final DisciplineRepository repository;

    public DisciplineService(DisciplineRepository repository) {
        this.repository = repository;
    }

    /**
     * Documentação:
     * Salva ou atualiza uma disciplina, aplicando regras de validação.
     * Regra 1: Não permite o cadastro de disciplinas com o mesmo nome dentro do mesmo curso.
     * @param discipline A disciplina a ser salva.
     * @return A disciplina salva.
     * @throws IllegalStateException se a regra de negócio for violada.
     * @throws IllegalArgumentException se o curso associado à disciplina for nulo.
     */
    @Transactional
    public Discipline save(Discipline discipline) {
        // Validação primária: Garante que a disciplina está associada a um curso.
        if (discipline.getCourse() == null || discipline.getCourse().getId() == null) {
            throw new IllegalArgumentException("A disciplina precisa estar associada a um curso válido.");
        }

        // Validação de duplicidade: Busca por nome dentro do mesmo curso.
        Optional<Discipline> existingDiscipline = repository.findByNameAndCourseId(discipline.getName(), discipline.getCourse().getId());

        if (existingDiscipline.isPresent() && !existingDiscipline.get().getId().equals(discipline.getId())) {
            // Lança exceção se já existe uma disciplina com esse nome no curso,
            // e não é a própria disciplina que estamos alterando.
            throw new IllegalStateException("Já existe uma disciplina com o nome '" + discipline.getName() + "' neste curso.");
        }

        return repository.save(discipline);
    }

    public List<Discipline> findAll() {
        return repository.findAll();
    }

    public List<Discipline> findByCourseId(UUID courseId) {
        return repository.findByCourseId(courseId);
    }

    public Optional<Discipline> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public void delete(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Disciplina não encontrada para o ID: " + id);
        }
    }
}