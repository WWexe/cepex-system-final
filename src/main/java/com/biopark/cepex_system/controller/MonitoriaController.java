package com.biopark.cepex_system.controller;

import com.biopark.cepex_system.domain.monitoria.Monitoria;
import com.biopark.cepex_system.domain.monitoria.MonitoriaUpdateDTO;
import com.biopark.cepex_system.domain.monitoria.StatusMonitoria;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.domain.user.UserRole;
import com.biopark.cepex_system.domain.monitoria.CandidaturaMonitoria;
import com.biopark.cepex_system.service.MonitoriaService;
import com.biopark.cepex_system.service.CandidaturaMonitoriaService;
import com.biopark.cepex_system.repository.CourseRepository;
import com.biopark.cepex_system.repository.DisciplineRepository;
import com.biopark.cepex_system.repository.ProfessorRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/monitorias")
@CrossOrigin(origins = "*")
public class MonitoriaController {

    private final MonitoriaService monitoriaService;
    private final CandidaturaMonitoriaService candidaturaMonitoriaService;
    private final CourseRepository courseRepository;
    private final DisciplineRepository disciplineRepository;
    private final ProfessorRepository professorRepository;

    public MonitoriaController(MonitoriaService monitoriaService, CandidaturaMonitoriaService candidaturaMonitoriaService, CourseRepository courseRepository, DisciplineRepository disciplineRepository, ProfessorRepository professorRepository) {
        this.monitoriaService = monitoriaService;
        this.candidaturaMonitoriaService = candidaturaMonitoriaService;
        this.courseRepository = courseRepository;
        this.disciplineRepository = disciplineRepository;
        this.professorRepository = professorRepository;
    }

    @PostMapping
    public ResponseEntity<Monitoria> createMonitoria(@RequestBody @Valid Monitoria monitoria) {
        // Garantir que o status seja sempre PENDENTE no cadastro
        monitoria.setStatusMonitoria(StatusMonitoria.PENDENTE);
        return ResponseEntity.ok(monitoriaService.save(monitoria));
    }

    @GetMapping
    public ResponseEntity<List<Monitoria>> getMonitorias(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal User user) {
        
        // Se for estudante, mostrar apenas monitorias aprovadas
        if (user != null && user.getRole() == UserRole.STUDENT) {
            List<Monitoria> monitorias = monitoriaService.findAllForStudent(search);
            return ResponseEntity.ok(monitorias);
        }
        
        // Para outros roles, mostrar todas as monitorias com filtros
        List<Monitoria> monitorias = monitoriaService.findAll(search, status);
        return ResponseEntity.ok(monitorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Monitoria> getMonitoriaById(@PathVariable UUID id) {
        Optional<Monitoria> monitoria = monitoriaService.findById(id);
        return monitoria.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Atualizar Monitoria
    @PutMapping("/{id}")
    public ResponseEntity<Monitoria> updateMonitoria(@PathVariable UUID id, @RequestBody @Valid MonitoriaUpdateDTO monitoriaDetails) {
        return monitoriaService.findById(id)
                .map(existingMonitoria -> {
                    // Buscar as entidades relacionadas
                    var course = courseRepository.findById(monitoriaDetails.getCourseId())
                            .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
                    var subject = disciplineRepository.findById(monitoriaDetails.getSubjectId())
                            .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));
                    var professor = professorRepository.findById(monitoriaDetails.getProfessorId())
                            .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

                    // Atualizar os campos
                    existingMonitoria.setTitle(monitoriaDetails.getTitle());
                    existingMonitoria.setDescription(monitoriaDetails.getDescription());
                    existingMonitoria.setRemote(monitoriaDetails.getRemote());
                    existingMonitoria.setLocation(monitoriaDetails.getLocation());
                    existingMonitoria.setVacancies(monitoriaDetails.getVacancies());
                    existingMonitoria.setWorkload(monitoriaDetails.getWorkload());
                    existingMonitoria.setInicialDate(monitoriaDetails.getInicialDate());
                    existingMonitoria.setFinalDate(monitoriaDetails.getFinalDate());
                    existingMonitoria.setInicialIngressDate(monitoriaDetails.getInicialIngressDate());
                    existingMonitoria.setFinalIngressDate(monitoriaDetails.getFinalIngressDate());
                    existingMonitoria.setSelectionType(monitoriaDetails.getSelectionType());
                    existingMonitoria.setSelectionDate(monitoriaDetails.getSelectionDate());
                    existingMonitoria.setSelectionTime(monitoriaDetails.getSelectionTime());
                    existingMonitoria.setDivulgationDate(monitoriaDetails.getDivulgationDate());
                    existingMonitoria.setStatusMonitoria(monitoriaDetails.getStatusMonitoria());
                    existingMonitoria.setCourse(course);
                    existingMonitoria.setSubject(subject);
                    existingMonitoria.setProfessor(professor);

                    Monitoria updatedMonitoria = monitoriaService.save(existingMonitoria);
                    return ResponseEntity.ok(updatedMonitoria);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Monitoria> deleteMonitoria(@PathVariable UUID id) {
        if (monitoriaService.findById(id).isPresent()) {
            monitoriaService.delete(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{monitoriaId}/candidatar")
    public ResponseEntity<CandidaturaMonitoria> candidatarMonitoria(
            @PathVariable UUID monitoriaId,
            @AuthenticationPrincipal User alunoAutenticado) {

        if (alunoAutenticado == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            CandidaturaMonitoria candidatura = candidaturaMonitoriaService.candidatar(monitoriaId, alunoAutenticado.getId());
            return ResponseEntity.ok(candidatura);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{monitoriaId}/candidatar")
    public ResponseEntity<Object> cancelarCandidaturaMonitoria(
            @PathVariable UUID monitoriaId,
            @AuthenticationPrincipal User alunoAutenticado) {

        if (alunoAutenticado == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            candidaturaMonitoriaService.cancelarCandidatura(monitoriaId, alunoAutenticado.getId());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            System.err.println("Erro ao cancelar candidatura: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{monitoriaId}/candidatura-status")
    public ResponseEntity<Boolean> checkCandidaturaStatus(
            @PathVariable UUID monitoriaId,
            @AuthenticationPrincipal User alunoAutenticado) {

        if (alunoAutenticado == null) {
            return ResponseEntity.status(401).build();
        }

        boolean hasCandidatura = candidaturaMonitoriaService.checkCandidaturaStatus(monitoriaId, alunoAutenticado.getId());
        return ResponseEntity.ok(hasCandidatura);
    }

    // Obter estatísticas de Monitoria
    @GetMapping("/statistics")
    public ResponseEntity<MonitoriaService.MonitoriaStatisticsDTO> getMonitoriaStatistics(
            @AuthenticationPrincipal User user) {

        // Verificar se o usuário tem permissão para ver estatísticas
        if (user == null || !user.getRole().equals(UserRole.ADMIN) &&
            !user.getRole().equals(UserRole.COORDENATION) &&
            !user.getRole().equals(UserRole.SECRETARY)) {
            return ResponseEntity.status(403).build();
        }

        MonitoriaService.MonitoriaStatisticsDTO stats = monitoriaService.getMonitoriaStatistics();
        return ResponseEntity.ok(stats);
    }

    // Classe interna para resposta de erro
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}