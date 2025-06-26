package com.biopark.cepex_system.repository;

import com.biopark.cepex_system.domain.course.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisciplineRepository extends JpaRepository<Discipline, UUID> {
    /**
     * Documentação:
     * Busca uma disciplina pelo nome e pelo ID do curso associado.
     * Essencial para validar se uma disciplina com o mesmo nome já existe
     * dentro de um curso específico, evitando duplicatas.
     * @param name O nome da disciplina.
     * @param course O UUID do curso.
     * @return um Optional contendo a disciplina se encontrada, ou um Optional vazio caso contrário.
     */
    Optional<Discipline> findByNameAndCourseId(String name, UUID course);

    /**
     * Busca todas as disciplinas de um curso específico.
     * @param courseId O UUID do curso.
     * @return Lista de disciplinas do curso.
     */
    List<Discipline> findByCourseId(UUID courseId);
}