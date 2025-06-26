package com.biopark.cepex_system.service;

import com.biopark.cepex_system.domain.project.ExtensionProject;
import com.biopark.cepex_system.domain.project.InscricaoExtensionProject;
import com.biopark.cepex_system.domain.project.StatusInscricaoProjeto;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.repository.ExtensionProjectRepository;
import com.biopark.cepex_system.repository.InscricaoExtensionProjectRepository;
import com.biopark.cepex_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço para gerenciar inscrições em Projetos de Extensão.
 * **Novo serviço**, com lógica similar à do serviço de inscrição em projeto de pesquisa,
 * mas operando sobre a entidade 'ExtensionProject'.
 */
@Service
public class InscricaoExtensionProjectService {

    private final InscricaoExtensionProjectRepository inscricaoRepository;
    private final ExtensionProjectRepository extensionProjectRepository;
    private final UserRepository userRepository;

    public InscricaoExtensionProjectService(InscricaoExtensionProjectRepository inscricaoRepository,
                                            ExtensionProjectRepository extensionProjectRepository,
                                            UserRepository userRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.extensionProjectRepository = extensionProjectRepository;
        this.userRepository = userRepository;
    }

    /**
     * Realiza a inscrição de um aluno em um projeto de extensão.
     * **Lógica similar à de pesquisa**, com verificações de status de candidaturas existentes.
     */
    @Transactional
    public InscricaoExtensionProject inscrever(UUID projectId, UUID alunoId) {
        ExtensionProject project = extensionProjectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projeto de Extensão não encontrado."));

        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        Optional<InscricaoExtensionProject> existingInscricao = inscricaoRepository.findByExtensionProjectAndAluno(project, aluno);
        if (existingInscricao.isPresent()) {
            InscricaoExtensionProject inscricao = existingInscricao.get();
            if (inscricao.getStatus() == StatusInscricaoProjeto.APROVADA) {
                throw new RuntimeException("Você já está aprovado para este projeto de extensão e não pode se inscrever novamente.");
            } else if (inscricao.getStatus() == StatusInscricaoProjeto.PENDENTE) {
                throw new RuntimeException("Você já possui uma inscrição pendente para este projeto de extensão.");
            } else if (inscricao.getStatus() == StatusInscricaoProjeto.CANCELADA || inscricao.getStatus() == StatusInscricaoProjeto.REJEITADA) {
                inscricao.setStatus(StatusInscricaoProjeto.PENDENTE);
                inscricao.setDataInscricao(java.time.LocalDateTime.now());
                return inscricaoRepository.save(inscricao);
            }
        }

        InscricaoExtensionProject novaInscricao = new InscricaoExtensionProject(project, aluno);
        return inscricaoRepository.save(novaInscricao);
    }

    /**
     * Permite que um aluno cancele sua inscrição em um projeto de extensão.
     * Não permite cancelar se a inscrição já estiver APROVADA.
     */
    @Transactional
    public void cancelarInscricao(UUID projectId, UUID alunoId) {
        ExtensionProject project = extensionProjectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projeto de Extensão não encontrado."));

        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        InscricaoExtensionProject inscricao = inscricaoRepository.findByExtensionProjectAndAluno(project, aluno)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada."));

        if (inscricao.getStatus() == StatusInscricaoProjeto.APROVADA) {
            throw new RuntimeException("Não é possível cancelar uma inscrição que já foi aprovada.");
        }

        inscricao.setStatus(StatusInscricaoProjeto.CANCELADA);
        inscricaoRepository.save(inscricao);
    }

    /**
     * Verifica o status de inscrição de um aluno em um projeto de extensão.
     * Retorna `true` se o aluno tem uma inscrição `PENDENTE` ou `APROVADA` para o projeto.
     */
    public boolean checkInscricaoStatus(UUID projectId, UUID alunoId) {
        return inscricaoRepository.findByExtensionProjectAndAluno(
                extensionProjectRepository.getReferenceById(projectId),
                userRepository.getReferenceById(alunoId)
        ).filter(i -> i.getStatus() == StatusInscricaoProjeto.PENDENTE || i.getStatus() == StatusInscricaoProjeto.APROVADA).isPresent();
    }

    public List<InscricaoExtensionProject> findInscricoesByExtensionProject(UUID projectId) {
        ExtensionProject project = extensionProjectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projeto de Extensão não encontrado."));
        return inscricaoRepository.findByExtensionProject(project);
    }

    public List<InscricaoExtensionProject> findInscricoesByAluno(UUID alunoId) {
        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));
        return inscricaoRepository.findByAluno(aluno);
    }
}