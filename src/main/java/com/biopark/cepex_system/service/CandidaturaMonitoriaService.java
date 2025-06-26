package com.biopark.cepex_system.service;

import com.biopark.cepex_system.domain.monitoria.CandidaturaMonitoria;
import com.biopark.cepex_system.domain.monitoria.Monitoria;
import com.biopark.cepex_system.domain.monitoria.StatusCandidatura;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.repository.CandidaturaMonitoriaRepository;
import com.biopark.cepex_system.repository.MonitoriaRepository;
import com.biopark.cepex_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CandidaturaMonitoriaService {

    private final CandidaturaMonitoriaRepository candidaturaRepository;
    private final MonitoriaRepository monitoriaRepository;
    private final UserRepository userRepository;

    public CandidaturaMonitoriaService(CandidaturaMonitoriaRepository candidaturaRepository,
                                       MonitoriaRepository monitoriaRepository,
                                       UserRepository userRepository) {
        this.candidaturaRepository = candidaturaRepository;
        this.monitoriaRepository = monitoriaRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CandidaturaMonitoria candidatar(UUID monitoriaId, UUID alunoId) {
        Monitoria monitoria = monitoriaRepository.findById(monitoriaId)
                .orElseThrow(() -> new RuntimeException("Monitoria não encontrada."));

        User aluno = (User) userRepository.findById(alunoId) // Cast para User
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        // Verificar se o aluno já se candidatou para esta monitoria
        Optional<CandidaturaMonitoria> existingCandidatura = candidaturaRepository.findByMonitoriaAndAluno(monitoria, aluno);
        if (existingCandidatura.isPresent()) {
            CandidaturaMonitoria candidatura = existingCandidatura.get();
            if (candidatura.getStatus() == StatusCandidatura.APROVADA) {
                throw new RuntimeException("Você já foi aprovado para esta monitoria e não pode se candidatar novamente.");
            } else if (candidatura.getStatus() == StatusCandidatura.PENDENTE) {
                throw new RuntimeException("Você já possui uma candidatura pendente para esta monitoria.");
            } else if (candidatura.getStatus() == StatusCandidatura.CANCELADA) {
                // Permite recandidatar se a anterior foi cancelada
                candidatura.setStatus(StatusCandidatura.PENDENTE);
                candidatura.setDataCandidatura(java.time.LocalDateTime.now());
                return candidaturaRepository.save(candidatura);
            } else if (candidatura.getStatus() == StatusCandidatura.REJEITADA) {
                // Permite recandidatar se a anterior foi rejeitada
                candidatura.setStatus(StatusCandidatura.PENDENTE);
                candidatura.setDataCandidatura(java.time.LocalDateTime.now());
                return candidaturaRepository.save(candidatura);
            }
        }

        // Criar nova candidatura
        CandidaturaMonitoria novaCandidatura = new CandidaturaMonitoria(monitoria, aluno);
        return candidaturaRepository.save(novaCandidatura);
    }

    @Transactional
    public void cancelarCandidatura(UUID monitoriaId, UUID alunoId) {
        System.out.println("Tentando cancelar candidatura - Monitoria ID: " + monitoriaId + ", Aluno ID: " + alunoId);
        
        try {
            Monitoria monitoria = monitoriaRepository.findById(monitoriaId)
                    .orElseThrow(() -> new RuntimeException("Monitoria não encontrada."));

            User aluno = (User) userRepository.findById(alunoId)
                    .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

            System.out.println("Monitoria encontrada: " + monitoria.getTitle());
            System.out.println("Aluno encontrado: " + aluno.getLogin());

            Optional<CandidaturaMonitoria> candidaturaOpt = candidaturaRepository.findByMonitoriaAndAluno(monitoria, aluno);
            
            if (candidaturaOpt.isEmpty()) {
                System.out.println("Candidatura não encontrada!");
                throw new RuntimeException("Candidatura não encontrada.");
            }

            CandidaturaMonitoria candidatura = candidaturaOpt.get();
            System.out.println("Candidatura encontrada - Status atual: " + candidatura.getStatus());

            if (candidatura.getStatus() == StatusCandidatura.APROVADA) {
                System.out.println("Tentativa de cancelar candidatura aprovada!");
                throw new RuntimeException("Não é possível cancelar uma candidatura que já foi aprovada.");
            }

            if (candidatura.getStatus() == StatusCandidatura.CANCELADA) {
                System.out.println("Candidatura já está cancelada!");
                throw new RuntimeException("Candidatura já está cancelada.");
            }

            candidatura.setStatus(StatusCandidatura.CANCELADA);
            candidaturaRepository.save(candidatura);
            System.out.println("Candidatura cancelada com sucesso!");
            
        } catch (Exception e) {
            System.err.println("Erro ao cancelar candidatura: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Metodo para verificar se um aluno já se candidatou ou foi aprovado para uma monitoria
    public boolean checkCandidaturaStatus(UUID monitoriaId, UUID alunoId) {
        return candidaturaRepository.findByMonitoriaAndAluno(
                monitoriaRepository.getReferenceById(monitoriaId),
                userRepository.getReferenceById(alunoId)
        ).filter(c -> c.getStatus() == StatusCandidatura.PENDENTE || c.getStatus() == StatusCandidatura.APROVADA).isPresent();
    }

    // Métodos para listar candidaturas
    public List<CandidaturaMonitoria> findCandidaturasByMonitoria(UUID monitoriaId) {
        Monitoria monitoria = monitoriaRepository.findById(monitoriaId)
                .orElseThrow(() -> new RuntimeException("Monitoria não encontrada."));
        return candidaturaRepository.findByMonitoria(monitoria);
    }

    public List<CandidaturaMonitoria> findCandidaturasByAluno(UUID alunoId) {
        User aluno = (User) userRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));
        return candidaturaRepository.findByAluno(aluno);
    }
}