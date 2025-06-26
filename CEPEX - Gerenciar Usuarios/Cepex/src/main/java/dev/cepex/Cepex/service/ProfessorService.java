package dev.cepex.Cepex.service;

import dev.cepex.Cepex.Model.Professor;
import dev.cepex.Cepex.Model.Perfil;
import dev.cepex.Cepex.Repository.ProfessorRepository;
import dev.cepex.Cepex.Repository.PerfilRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService extends UsuarioService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    private static final String COORDENADOR_PERFIL_NOME = "ROLE_COORDENADOR";

    public List<Professor> listarTodosProfessores() {
        return professorRepository.findAll();
    }

    public Optional<Professor> buscarProfessorPorId(Integer id) {
        return professorRepository.findById(id);
    }

    public Professor salvar(Professor professor){ return professorRepository.save(professor); }


    @Transactional
    public void deletarProfessor(Integer id) {
        if (!professorRepository.existsById(id)) {
            throw new EntityNotFoundException("Professor não encontrado com ID: " + id + " para exclusão.");
        }
        professorRepository.deleteById(id);
    }

    /*@Transactional
    public Professor salvarOuAtualizarProfessor(Professor professor) {
        if (professor.getId() == null && professor.getRa() != null && !professor.getRa().isEmpty()) {
            professorRepository.findByRa(professor.getRa()).ifPresent(p -> {
                throw new IllegalArgumentException("RA de professor já cadastrado: " + professor.getRa());
            });
        }

        Perfil perfilCoordenador = perfilRepository.findByNome(COORDENADOR_PERFIL_NOME)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Perfil '" + COORDENADOR_PERFIL_NOME + "' não encontrado no sistema. " +
                                "Por favor, cadastre este perfil."
                ));

        if (professor.isCoordenador()) {
            professor.addPerfil(perfilCoordenador);
        } else {
            professor.removerPerfil(perfilCoordenador);
        }

        Professor professorSalvo = (Professor) super.salvar(professor);

        return professorSalvo;
    }*/
}
