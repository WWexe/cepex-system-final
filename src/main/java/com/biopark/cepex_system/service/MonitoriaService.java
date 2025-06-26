package com.biopark.cepex_system.service;

import com.biopark.cepex_system.domain.monitoria.Monitoria;
import com.biopark.cepex_system.domain.monitoria.StatusCandidatura;
import com.biopark.cepex_system.domain.monitoria.StatusMonitoria;
import com.biopark.cepex_system.repository.MonitoriaRepository;
import com.biopark.cepex_system.repository.CandidaturaMonitoriaRepository;
import com.biopark.cepex_system.domain.monitoria.CandidaturaMonitoria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MonitoriaService {

    private final MonitoriaRepository repository;
    private final CandidaturaMonitoriaRepository candidaturaRepository;

    public MonitoriaService(MonitoriaRepository repository, CandidaturaMonitoriaRepository candidaturaRepository) {
        this.repository = repository;
        this.candidaturaRepository = candidaturaRepository;
    }

    @Transactional
    public Monitoria save(Monitoria monitoria) {
        return repository.save(monitoria);
    }

    public List<Monitoria> findAll(String search, String status) {
        List<Monitoria> monitorias = repository.findAll();

        if (search != null && !search.trim().isEmpty()) {
            String lowerCaseSearch = search.trim().toLowerCase();
            monitorias = monitorias.stream()
                    .filter(m -> m.getTitle().toLowerCase().contains(lowerCaseSearch) ||
                            (m.getProfessor() != null && m.getProfessor().getFirstName().toLowerCase().contains(lowerCaseSearch)) ||
                            (m.getSubject() != null && m.getSubject().getName().toLowerCase().contains(lowerCaseSearch)))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("TODOS")) {
            try {
                StatusMonitoria statusEnum = StatusMonitoria.valueOf(status.toUpperCase());
                monitorias = monitorias.stream()
                        .filter(m -> m.getStatusMonitoria() == statusEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Tratar caso o status fornecido não seja um enum válido
                // Ou simplesmente ignorar o filtro de status
            }
        }

        return monitorias;
    }

    public List<Monitoria> findAllForStudent(String search) {
        List<Monitoria> monitorias = repository.findAll();

        // Filtrar apenas monitorias aprovadas para estudantes
        monitorias = monitorias.stream()
                .filter(m -> m.getStatusMonitoria() == StatusMonitoria.APROVADA)
                .collect(Collectors.toList());

        if (search != null && !search.trim().isEmpty()) {
            String lowerCaseSearch = search.trim().toLowerCase();
            monitorias = monitorias.stream()
                    .filter(m -> m.getTitle().toLowerCase().contains(lowerCaseSearch) ||
                            (m.getProfessor() != null && m.getProfessor().getFirstName().toLowerCase().contains(lowerCaseSearch)) ||
                            (m.getSubject() != null && m.getSubject().getName().toLowerCase().contains(lowerCaseSearch)))
                    .collect(Collectors.toList());
        }

        return monitorias;
    }

    public Optional<Monitoria> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public void delete(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Monitoria não encontrada para o ID: " + id);
        }
    }

    public MonitoriaStatisticsDTO getMonitoriaStatistics() {
        List<Monitoria> allMonitorias = repository.findAll();
        List<CandidaturaMonitoria> allCandidaturas = candidaturaRepository.findAll();

        // Monitorias abertas: todas exceto CANCELADA e REJEITADA
        long monitoriasAbertas = allMonitorias.stream()
                .filter(m -> m.getStatusMonitoria() != StatusMonitoria.CANCELADA && 
                           m.getStatusMonitoria() != StatusMonitoria.REJEITADA)
                .count();

        // Monitores ativos: candidaturas aprovadas
        long monitoresAtivos = allCandidaturas.stream()
                .filter(c -> c.getStatus() == StatusCandidatura.APROVADA)
                .count();

        // Candidatos pendentes: candidaturas com status pendente
        long candidatosPendentes = allCandidaturas.stream()
                .filter(c -> c.getStatus() == StatusCandidatura.PENDENTE)
                .count();

        return new MonitoriaStatisticsDTO(monitoriasAbertas, monitoresAtivos, candidatosPendentes);
    }

    public record MonitoriaStatisticsDTO(long monitoriasAbertas, long monitoresAtivos, long candidatosPendentes) {}
}