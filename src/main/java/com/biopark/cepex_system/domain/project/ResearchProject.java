package com.biopark.cepex_system.domain.project;

import com.biopark.cepex_system.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "research_projects")
public class ResearchProject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O título do projeto não pode estar em branco.")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status",  nullable = false)
    private ProjectStatus status;

    @NotNull(message = "O pesquisador líder não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "lead_researcher_id", nullable = false)
    private User leadResearcher;

    @ManyToMany
    @JoinTable(
            name = "project_collaborators",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> collaborators = new HashSet<>();

    // Novos campos solicitados
    @Column(name = "material_usage", columnDefinition = "TEXT")
    private String materialUsage;

    @Column(name = "research_line", columnDefinition = "TEXT")
    private String researchLine;

    // Campos de informações do projeto
    @Column(name = "subject_theme", columnDefinition = "TEXT")
    private String subjectTheme;

    @Column(name = "justification", columnDefinition = "TEXT")
    private String justification;

    @Column(name = "problem_formulation", columnDefinition = "TEXT")
    private String problemFormulation;

    @Column(name = "hypothesis_formulation", columnDefinition = "TEXT")
    private String hypothesisFormulation;

    @Column(name = "general_objective", columnDefinition = "TEXT")
    private String generalObjective;

    @Column(name = "specific_objective", columnDefinition = "TEXT")
    private String specificObjective;

    @Column(name = "theoretical_foundation", columnDefinition = "TEXT")
    private String theoreticalFoundation;

    @Column(name = "methodological_approaches", columnDefinition = "TEXT")
    private String methodologicalApproaches;

    @Column(name = "project_references", columnDefinition = "TEXT")
    private String projectReferences;

    public enum ProjectStatus {
        ABERTO,
        ANALISE,
        COMPLETO,
        CANCELADO
    }
}
