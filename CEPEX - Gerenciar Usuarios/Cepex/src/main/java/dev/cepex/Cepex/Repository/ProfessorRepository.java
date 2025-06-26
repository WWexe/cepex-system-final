package dev.cepex.Cepex.Repository;

import dev.cepex.Cepex.Model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Integer> {

    // Este método é válido pois, conforme definido, a entidade Professor herda o campo 'ra'.
    Optional<Professor> findByRa(String ra);

    // Este método também está correto.
    Optional<Professor> findByEmail(String email);

}
