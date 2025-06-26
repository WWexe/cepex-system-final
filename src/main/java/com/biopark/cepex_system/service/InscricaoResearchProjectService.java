package com.biopark.cepex_system.service;

import com.biopark.cepex_system.domain.project.InscricaoResearchProject;
import com.biopark.cepex_system.domain.project.ResearchProject;
import com.biopark.cepex_system.domain.project.StatusInscricaoProjeto;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.repository.InscricaoResearchProjectRepository;
import com.biopark.cepex_system.repository.ResearchProjectRepository;
import com.biopark.cepex_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar inscrições em Projetos de Pesquisa.
 * **Novo serviço** que substitui a lógica genérica de inscrição do ProjetoService anterior.
 * Contém regras de negócio para a criação e cancelamento de inscrições.
 */
@Service
public class InscricaoResearchProjectService {

    private final InscricaoResearchProjectRepository inscricaoRepository;
    private final ResearchProjectRepository researchProjectRepository;
    private final UserRepository userRepository;

    public InscricaoResearchProjectService(InscricaoResearchProjectRepository inscricaoRepository,
                                           ResearchProjectRepository researchProjectRepository,
                                           UserRepository userRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.researchProjectRepository = researchProjectRepository;
        this.userRepository = userRepository;
    }

    /**
     * Realiza a inscrição de um aluno em um projeto de pesquisa.
     * **Lógica complexa**:
     * 1. Verifica se projeto e aluno existem.
     * 2. Procura por inscrição existente.
     * 3. Se existir:
     * - Lança erro se já APROVADA ou PENDENTE.
     * - Permite reinscrever se CANCELADA ou REJEITADA, resetando o status para PENDENTE e data.
     * 4. Se não existir, cria uma nova inscrição com status PENDENTE.
     */
    @Transactional
    public InscricaoResearchProject inscrever(UUID projectId, UUID alunoId) {
        ResearchProject project = researchProjectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projeto de Pesquisa não encontrado."));

        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        Optional<InscricaoResearchProject> existingInscricao = inscricaoRepository.findByResearchProjectAndAluno(project, aluno);
        if (existingInscricao.isPresent()) {
            InscricaoResearchProject inscricao = existingInscricao.get();
            if (inscricao.getStatus() == StatusInscricaoProjeto.APROVADA) {
                throw new RuntimeException("Você já está aprovado para este projeto de pesquisa e não pode se inscrever novamente.");
            } else if (inscricao.getStatus() == StatusInscricaoProjeto.PENDENTE) {
                throw new RuntimeException("Você já possui uma inscrição pendente para este projeto de pesquisa.");
            } else if (inscricao.getStatus() == StatusInscricaoProjeto.CANCELADA || inscricao.getStatus() == StatusInscricaoProjeto.REJEITADA) {
                // Permite recandidatar se a anterior foi cancelada ou rejeitada
                inscricao.setStatus(StatusInscricaoProjeto.PENDENTE);
                inscricao.setDataInscricao(java.time.LocalDateTime.now());
                return inscricaoRepository.save(inscricao);
            }
        }

        InscricaoResearchProject novaInscricao = new InscricaoResearchProject(project, aluno);
        return inscricaoRepository.save(novaInscricao);
    }

    /**
     * Permite que um aluno cancele sua inscrição em um projeto de pesquisa.
     * **Lógica importante**: Não permite cancelar se a inscrição já estiver APROVADA.
     */
    @Transactional
    public void cancelarInscricao(UUID projectId, UUID alunoId) {
        ResearchProject project = researchProjectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projeto de Pesquisa não encontrado."));

        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        InscricaoResearchProject inscricao = inscricaoRepository.findByResearchProjectAndAluno(project, aluno)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada."));

        if (inscricao.getStatus() == StatusInscricaoProjeto.APROVADA) {
            throw new RuntimeException("Não é possível cancelar uma inscrição que já foi aprovada.");
        }

        inscricao.setStatus(StatusInscricaoProjeto.CANCELADA);
        inscricaoRepository.save(inscricao);
    }

    /**
     * Verifica o status de inscrição de um aluno em um projeto de pesquisa.
     * Retorna `true` se o aluno tem uma inscrição `PENDENTE` ou `APROVADA` para o projeto.
     */
    public boolean checkInscricaoStatus(UUID projectId, UUID alunoId) {
        return inscricaoRepository.findByResearchProjectAndAluno(
                researchProjectRepository.getReferenceById(projectId),
                userRepository.getReferenceById(alunoId)
        ).filter(i -> i.getStatus() == StatusInscricaoProjeto.PENDENTE || i.getStatus() == StatusInscricaoProjeto.APROVADA).isPresent();
    }

    // Métodos de listagem de inscrições, úteis para administradores ou para o perfil do aluno.
    public List<InscricaoResearchProject> findInscricoesByResearchProject(UUID projectId) {
        ResearchProject project = researchProjectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projeto de Pesquisa não encontrado."));
        return inscricaoRepository.findByResearchProject(project);
    }

    public List<InscricaoResearchProject> findInscricoesByAluno(UUID alunoId) {
        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));
        return inscricaoRepository.findByAluno(aluno);
    }

    /**
     * Busca todos os projetos em que um aluno está inscrito.
     * Este método utiliza o findInscricoesByAluno e mapeia os resultados
     * para retornar uma lista de ResearchProject.
     * @param aluno O usuário (aluno) para o qual buscar os projetos.
     * @return Uma lista de ResearchProject.
     */
    public List<ResearchProject> findProjectsByAluno(User aluno) {
        List<InscricaoResearchProject> inscricoes = inscricaoRepository.findByAluno(aluno);
        return inscricoes.stream()
                .map(InscricaoResearchProject::getResearchProject)
                .collect(Collectors.toList());
    }
}