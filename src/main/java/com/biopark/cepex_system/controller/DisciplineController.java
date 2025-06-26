package com.biopark.cepex_system.controller;

import com.biopark.cepex_system.domain.course.Discipline;
import com.biopark.cepex_system.service.DisciplineService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/disciplines")
@CrossOrigin(origins = "*")
public class DisciplineController {

    private final DisciplineService service;

    public DisciplineController(DisciplineService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Discipline> createDiscipline(@RequestBody @Valid Discipline discipline) {
        return ResponseEntity.ok(service.save(discipline));
    }

    @GetMapping
    public ResponseEntity<List<Discipline>> getAllDisciplines(
            @RequestParam(required = false) UUID courseId) {
        if (courseId != null) {
            List<Discipline> disciplines = service.findByCourseId(courseId);
            return ResponseEntity.ok(disciplines);
        }
        List<Discipline> disciplines = service.findAll();
        return ResponseEntity.ok(disciplines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Discipline> getDisciplineById(@PathVariable UUID id) {
        Optional<Discipline> discipline = service.findById(id);
        return discipline.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Atualizar Disciplina
    @PutMapping("/{id}")
    public ResponseEntity<Discipline> updateDiscipline(@PathVariable UUID id, @RequestBody Discipline disciplineDetails) {
        return service.findById(id)
                .map(existingDiscipline -> {
                    existingDiscipline.setName(disciplineDetails.getName());
                    existingDiscipline.setActive(disciplineDetails.getActive());
                    Discipline updatedDiscipline = service.save(existingDiscipline);
                    return ResponseEntity.ok(updatedDiscipline);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscipline(@PathVariable UUID id) {
        if (service.findById(id).isPresent()) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}