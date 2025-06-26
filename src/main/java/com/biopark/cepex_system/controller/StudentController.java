package com.biopark.cepex_system.controller;

import com.biopark.cepex_system.domain.student.Student;
import com.biopark.cepex_system.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody @Valid Student student) {
        return ResponseEntity.ok(service.save(student));
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = service.findAll();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable UUID id) {
        Optional<Student> student = service.findById(id);
        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ra/{ra}")
    public ResponseEntity<Student> getStudentByRa(@PathVariable String ra) {
        Optional<Student> student = service.findByRa(ra);
        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Student> getStudentByCpf(@PathVariable String cpf) {
        Optional<Student> student = service.findByCpf(cpf);
        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Student> getStudentByEmail(@PathVariable String email) {
        Optional<Student> student = service.findByEmail(email);
        return student.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable UUID id, @RequestBody @Valid Student studentDetails) {
        return service.findById(id)
                .map(existingStudent -> {
                    existingStudent.setFirstName(studentDetails.getFirstName());
                    existingStudent.setLastName(studentDetails.getLastName());
                    existingStudent.setEmail(studentDetails.getEmail());
                    existingStudent.setNumber(studentDetails.getNumber());
                    existingStudent.setRa(studentDetails.getRa());
                    existingStudent.setCpf(studentDetails.getCpf());
                    existingStudent.setStatus(studentDetails.getStatus());
                    existingStudent.setUser(studentDetails.getUser());
                    existingStudent.setCourse(studentDetails.getCourse());
                    
                    Student updatedStudent = service.save(existingStudent);
                    return ResponseEntity.ok(updatedStudent);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Student> updateStudentStatus(@PathVariable UUID id, @RequestParam boolean active) {
        return service.updateStatus(id, active)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        if (service.findById(id).isPresent()) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 