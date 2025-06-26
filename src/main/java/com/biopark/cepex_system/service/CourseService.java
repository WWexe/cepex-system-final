package com.biopark.cepex_system.service;

import com.biopark.cepex_system.domain.course.Course;
import com.biopark.cepex_system.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseRepository repository;

    public CourseService(CourseRepository repository) {
        this.repository = repository;
    }

    /**
     * Documentação:
     * Salva um novo curso ou atualiza um existente após aplicar as regras de validação.
     * Regra 1: O nome do curso não pode ser duplicado.
     *
     * @param course O objeto Course a ser salvo.
     * @return O objeto Course salvo.
     * @throws IllegalStateException se o nome do curso já existir.
     */

    @Transactional
    public Course save(Course course) {
        // Validação: Verifica se já existe um curso com o mesmo nome.
        Optional<Course> existingCourse = repository.findByName(course.getName());

        if (existingCourse.isPresent() && !existingCourse.get().getId().equals(course.getId())) {
            // Se encontrou um curso com o mesmo nome e o ID é diferente (evita erro na própria alteração)
            throw new IllegalStateException("Já existe um curso cadastrado com o nome: " + course.getName());
        }

        // Se passar na validação, salva no banco de dados.
        return repository.save(course);
    }

    public List<Course> findAll() {
        return repository.findAll();
    }

    public Optional<Course> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public void delete(UUID id) {
        // Validação 1: Busca o curso. Se não existir, o 'orElseThrow' lança a exceção.
        Course courseToDelete = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado para o ID: " + id));
        // Se todas as validações passarem, deleta o curso.
        repository.delete(courseToDelete);
    }

    /**
     * Busca todos os cursos que estão ativos no sistema.
     * 
     * @return Lista de cursos ativos.
     */
    public List<Course> findActiveCourses() {
        return repository.findByActiveTrue();
    }
}