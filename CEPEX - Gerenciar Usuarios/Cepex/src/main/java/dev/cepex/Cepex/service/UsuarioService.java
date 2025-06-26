package dev.cepex.Cepex.service;

import dev.cepex.Cepex.Dto.UsuarioDTO;
import dev.cepex.Cepex.Model.Aluno;
import dev.cepex.Cepex.Model.ContaUsuario;
import dev.cepex.Cepex.Model.Professor;
import dev.cepex.Cepex.Repository.AlunoRepository;
import dev.cepex.Cepex.Repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private AlunoRepository alunoRepository;
    @Autowired
    private ProfessorRepository professorRepository;

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        List<UsuarioDTO> todos = new ArrayList<>();
        alunoRepository.findAll().forEach(aluno -> todos.add(toAlunoDTO(aluno)));
        professorRepository.findAll().forEach(prof -> todos.add(toProfessorDTO(prof)));
        return todos;
    }

    @Transactional(readOnly = true)
    public Page<UsuarioDTO> listarPaginado(int page, int size) {
        List<UsuarioDTO> todosOsUsuarios = listarTodos();
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), todosOsUsuarios.size());

        List<UsuarioDTO> paginaDeConteudo = start > todosOsUsuarios.size() ? Collections.emptyList() : todosOsUsuarios.subList(start, end);

        return new PageImpl<>(paginaDeConteudo, pageable, todosOsUsuarios.size());
    }

    @Transactional
    public UsuarioDTO salvar(UsuarioDTO dto) {
        if ("Aluno".equalsIgnoreCase(dto.getTipo())) {
            Aluno aluno = fromDTOtoAluno(dto);
            Aluno salvo = alunoRepository.save(aluno);
            return toAlunoDTO(salvo);
        } else if ("Professor".equalsIgnoreCase(dto.getTipo())) {
            Professor professor = fromDTOtoProfessor(dto);
            Professor salvo = professorRepository.save(professor);
            return toProfessorDTO(salvo);
        } else {
            throw new IllegalArgumentException("Tipo de usuário inválido: " + dto.getTipo());
        }
    }

    @Transactional
    public void excluir(Integer id) {
        if (professorRepository.existsById(id)) {
            professorRepository.deleteById(id);
        } else if (alunoRepository.existsById(id)) {
            alunoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuário não encontrado com o ID: " + id);
        }
    }

    private Aluno fromDTOtoAluno(UsuarioDTO dto) {
        Aluno aluno;
        if (dto.getId() != null) {
            aluno = alunoRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Aluno não encontrado com ID: " + dto.getId()));
        } else {
            aluno = new Aluno();
        }

        ContaUsuario user = new ContaUsuario();
        user.setStUser(true);
        user.setPasswordHash("");
        // --- CORREÇÃO AQUI: Define o login usando o RA do DTO ---
        user.setLogin(dto.getRa());

        aluno.setFirstname(dto.getFirstname());
        aluno.setLastname(dto.getLastname());
        aluno.setEmail(dto.getEmail());
        aluno.setCpf(dto.getCpf());
        aluno.setRa(dto.getRa());
        aluno.setUser(user);

        return aluno;
    }

    private Professor fromDTOtoProfessor(UsuarioDTO dto) {
        Professor professor;
        if (dto.getId() != null) {
            professor = professorRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado com ID: " + dto.getId()));
        } else {
            professor = new Professor();
        }

        ContaUsuario user = new ContaUsuario();
        user.setStUser(true);
        user.setPasswordHash("");
        // --- CORREÇÃO AQUI: Define o login usando o RA do DTO ---
        user.setLogin(dto.getRa());

        professor.setFirstname(dto.getFirstname());
        professor.setLastname(dto.getLastname());
        professor.setEmail(dto.getEmail());
        professor.setCpf(dto.getCpf());
        professor.setRa(dto.getRa());
        professor.setUser(user);

        return professor;
    }

    private UsuarioDTO toAlunoDTO(Aluno aluno) {
        if (aluno == null) return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(aluno.getId());
        dto.setFirstname(aluno.getFirstname());
        dto.setLastname(aluno.getLastname());
        dto.setEmail(aluno.getEmail());
        dto.setCpf(aluno.getCpf());
        dto.setRa(aluno.getRa());
        dto.setTipo("Aluno");
        return dto;
    }

    private UsuarioDTO toProfessorDTO(Professor professor) {
        if (professor == null) return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(professor.getId());
        dto.setFirstname(professor.getFirstname());
        dto.setLastname(professor.getLastname());
        dto.setEmail(professor.getEmail());
        dto.setCpf(professor.getCpf());
        dto.setRa(professor.getRa());
        dto.setTipo("Professor");
        return dto;
    }
}
