package com.biopark.cepex_system.domain.course;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "discipline")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Discipline {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O nome da disciplina não pode estar em branco.")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "A disciplina conter um status")
    @Column(name = "st_discipline")
    @ColumnDefault("true")
    private Boolean active;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull(message = "A disciplina deve estar associada a um curso.")
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

}