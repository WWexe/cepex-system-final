package dev.cepex.Cepex.Repository;

import dev.cepex.Cepex.Model.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlunoRepository extends JpaRepository<Aluno, Integer> {

    // QUERY NATIVA CORRIGIDA
    // **IMPORTANTE:** Substitua os nomes das colunas (ex: 'aluno_id', 'nome_aluno') pelos nomes exatos do seu banco de dados.
    @Query(
            value = "SELECT " +
                    "    a.aluno_id AS id, " +
                    "    a.nome_aluno AS firstname, " +
                    "    a.sobrenome_aluno AS lastname, " +
                    "    a.email_academico AS email, " +
                    "    'Aluno' as tipo " +
                    "FROM aluno a " + // Substitua 'aluno' pelo nome da sua tabela de alunos
                    "UNION ALL " +
                    "SELECT " +
                    "    p.professor_id AS id, " +
                    "    p.nome_prof AS firstname, " +
                    "    p.sobrenome_prof AS lastname, " +
                    "    p.email_corporativo AS email, " +
                    "    'Professor' as tipo " +
                    "FROM professor p", // Substitua 'professor' pelo nome da sua tabela de professores

            // O countQuery também precisa usar os nomes corretos das colunas de ID
            countQuery = "SELECT count(*) FROM (" +
                    "  SELECT aluno_id FROM aluno " +
                    "  UNION ALL " +
                    "  SELECT professor_id FROM professor" +
                    ") as total_count",

            nativeQuery = true
    )
    Page<UsuarioProjection> findTodosUsuariosPaginado(Pageable pageable);
}
