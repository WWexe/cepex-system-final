package com.biopark.cepex_system.domain.monitoria;

import java.time.LocalDate;
import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import com.biopark.cepex_system.domain.course.Course;
import com.biopark.cepex_system.domain.professor.Professor;
import com.biopark.cepex_system.domain.course.Discipline;

@Table(name = "monitoria")
@Entity(name = "Monitoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Monitoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O título da monitoria não pode estar em branco.")
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @NotNull(message = "O campo remoto não pode ser nulo.")
    @Column(name = "is_remote", nullable = false)
    private Boolean remote;

    @Column(name = "location")
    private String location;

    @NotNull(message = "O número de vagas não pode ser nulo.")
    @Column(name = "vacancies", nullable = false)
    private Integer vacancies;

    @NotNull(message = "A carga horária não pode ser nula.")
    @Column(name = "workload", nullable = false)
    private Integer workload;

    @NotNull
    @Column(name = "inicial_date", nullable = false)
    private LocalDate inicialDate;

    @NotNull
    @Column(name = "final_date", nullable = false)
    private LocalDate finalDate;

    @NotNull
    @Column(name = "inicial_ingress",  nullable = false)
    private LocalDate inicialIngressDate;

    @NotNull(message = "A data final de ingresso não pode ser nula.")
    @Column(name = "final_ingress", nullable = false)
    private LocalDate finalIngressDate;

    @NotNull(message = "O tipo de seleção não pode ser nulo.")
    @Enumerated(EnumType.STRING)
    @Column(name = "selection_type",  nullable = false)
    private SelectionType selectionType;

    @Column(name = "selection_date")
    private LocalDate selectionDate;

    @Column(name = "selection_time")
    private String selectionTime;

    @Column(name = "divulgation_date")
    private LocalDate divulgationDate;

    @NotNull(message = "O status da monitoria não pode ser nulo.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusMonitoria statusMonitoria;

    @NotNull(message = "A monitoria deve estar associada a um curso.")
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @NotNull(message = "A monitoria deve estar associada a uma disciplina.")
    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private Discipline subject;

    @NotNull(message = "A monitoria deve estar associada a um professor.")
    @ManyToOne
    @JoinColumn(name = "professor_id")
    private  Professor professor;
}
