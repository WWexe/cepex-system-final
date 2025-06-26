package com.biopark.cepex_system.controller;

import com.biopark.cepex_system.domain.professor.Professor;
import com.biopark.cepex_system.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/professors")
@CrossOrigin(origins = "*")
public class ProfessorController {

    private final ProfessorService service;

    public ProfessorController(ProfessorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Professor> createProfessor(@RequestBody @Valid Professor professor) {
        return ResponseEntity.ok(service.save(professor));
    }

    @GetMapping
    public ResponseEntity<List<Professor>> getAllProfessors() {
        List<Professor> professors = service.findAll();
        return ResponseEntity.ok(professors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professor> getProfessorById(@PathVariable UUID id) {
        Optional<Professor> professor = service.findById(id);
        return professor.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ra/{ra}")
    public ResponseEntity<Professor> getProfessorByRa(@PathVariable String ra) {
        Optional<Professor> professor = service.findByRa(ra);
        return professor.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Professor> getProfessorByCpf(@PathVariable String cpf) {
        Optional<Professor> professor = service.findByCpf(cpf);
        return professor.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Professor> getProfessorByEmail(@PathVariable String email) {
        Optional<Professor> professor = service.findByEmail(email);
        return professor.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Atualizar Professor
    @PutMapping("/{id}")
    public ResponseEntity<Professor> updateProfessor(@PathVariable UUID id, @RequestBody @Valid Professor professorDetails) {
        return service.findById(id)
                .map(existingProfessor -> {
                    existingProfessor.setFirstName(professorDetails.getFirstName());
                    existingProfessor.setLastName(professorDetails.getLastName());
                    existingProfessor.setEmail(professorDetails.getEmail());
                    existingProfessor.setRa(professorDetails.getRa());
                    existingProfessor.setCpf(professorDetails.getCpf());
                    existingProfessor.setNumber(professorDetails.getNumber());
                    existingProfessor.setActive(professorDetails.getActive());
                    existingProfessor.setCreatedAt(professorDetails.getCreatedAt());
                    existingProfessor.setDisciplines(professorDetails.getDisciplines());
                    Professor updatedProfessor = service.save(existingProfessor);
                    return ResponseEntity.ok(updatedProfessor);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Professor> updateProfessorStatus(@PathVariable UUID id, @RequestParam boolean active) {
        return service.updateStatus(id, active)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(@PathVariable UUID id) {
        if (service.findById(id).isPresent()) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}