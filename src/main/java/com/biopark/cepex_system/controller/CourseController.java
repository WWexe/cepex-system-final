package com.biopark.cepex_system.controller;

import com.biopark.cepex_system.domain.course.Course;
import com.biopark.cepex_system.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("courses")
@Tag(name = "Cursos", description = "Endpoints para gerenciamento de cursos")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    @Operation(
        summary = "Listar todos os cursos",
        description = "Retorna uma lista de todos os cursos cadastrados no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de cursos retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Course.class)
            )
        )
    })
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.findAll();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar curso por ID",
        description = "Retorna um curso específico pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Curso encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Course.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Curso não encontrado"
        )
    })
    public ResponseEntity<Course> getCourseById(
        @Parameter(
            description = "ID do curso",
            required = true,
            example = "660e8400-e29b-41d4-a716-446655440001"
        )
        @PathVariable UUID id
    ) {
        Optional<Course> course = courseService.findById(id);
        return course.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
        summary = "Criar novo curso",
        description = "Cria um novo curso no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Curso criado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Course.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos"
        )
    })
    public ResponseEntity<Course> createCourse(
        @Parameter(
            description = "Dados do curso",
            required = true
        )
        @RequestBody @Valid Course course
    ) {
        Course savedCourse = courseService.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar curso",
        description = "Atualiza os dados de um curso existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Curso atualizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Course.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Curso não encontrado"
        )
    })
    public ResponseEntity<Course> updateCourse(
        @Parameter(
            description = "ID do curso",
            required = true
        )
        @PathVariable UUID id,
        @Parameter(
            description = "Dados atualizados do curso",
            required = true
        )
        @RequestBody @Valid Course course
    ) {
        Optional<Course> existingCourse = courseService.findById(id);
        if (existingCourse.isPresent()) {
            course.setId(id);
            Course updatedCourse = courseService.save(course);
            return ResponseEntity.ok(updatedCourse);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar curso",
        description = "Remove um curso do sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Curso deletado com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Curso não encontrado"
        )
    })
    public ResponseEntity<Void> deleteCourse(
        @Parameter(
            description = "ID do curso",
            required = true
        )
        @PathVariable UUID id
    ) {
        try {
            courseService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/active")
    @Operation(
        summary = "Listar cursos ativos",
        description = "Retorna apenas os cursos que estão ativos no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de cursos ativos retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Course.class)
            )
        )
    })
    public ResponseEntity<List<Course>> getActiveCourses() {
        List<Course> activeCourses = courseService.findActiveCourses();
        return ResponseEntity.ok(activeCourses);
    }
}