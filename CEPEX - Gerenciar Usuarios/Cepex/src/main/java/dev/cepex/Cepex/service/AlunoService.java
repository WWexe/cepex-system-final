package dev.cepex.Cepex.service;

import dev.cepex.Cepex.Model.Aluno;
import dev.cepex.Cepex.Repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    public List<Aluno> listarTodos(){ return alunoRepository.findAll(); }

    public Aluno buscarPorId(Integer id){ return alunoRepository.findById(id).orElse(null); }

    public Aluno salvar(Aluno aluno){ return alunoRepository.save(aluno); }

    public void deletar(Integer id){ alunoRepository.deleteById(id); }

}
