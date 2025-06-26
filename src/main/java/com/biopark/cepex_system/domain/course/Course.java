package com.biopark.cepex_system.domain.course;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O nome do curso não pode estar em branco.")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "O número de semestres não pode ser nulo.")
    @Column(name = "semesters", nullable = false)
    private Integer semesters;

    @NotNull(message = "O status do curso não pode ser nulo.")
    @Column(name = "st_course", nullable = false)
    private Boolean active;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
