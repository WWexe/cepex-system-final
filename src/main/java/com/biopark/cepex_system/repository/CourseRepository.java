package com.biopark.cepex_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.biopark.cepex_system.domain.course.Course;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface CourseRepository  extends JpaRepository<Course, UUID> {
    /**
     * Documentação:
     * Busca um curso pelo seu nome.
     * É utilizado para verificar se um curso com o mesmo nome já existe no banco de dados,
     * garantindo que os nomes dos cursos sejam únicos.
     * @param name O nome do curso a ser pesquisado.
     * @return um Optional contendo o curso se encontrado, ou um Optional vazio caso contrário.
     */
    Optional<Course> findByName(String name);

    /**
     * Busca todos os cursos que estão ativos no sistema.
     * 
     * @return Lista de cursos ativos.
     */
    List<Course> findByActiveTrue();
}
