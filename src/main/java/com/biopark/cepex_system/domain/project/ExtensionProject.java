package com.biopark.cepex_system.domain.project;

import com.biopark.cepex_system.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "extension_projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionProject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O título do projeto não pode estar em branco.")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "A localização não pode estar em branco.")
    @Column(nullable = false)
    private String location;

    @Column(name = "target_beneficiaries")
    private String targetBeneficiaries;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @NotNull(message = "O coordenador não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "coordinator_id", nullable = false)
    private User coordinator;

    @ManyToOne
    @JoinColumn(name = "lead_researcher_id")
    private User leadResearcher;

    @ManyToMany
    @JoinTable(
            name = "extension_project_team",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> team = new HashSet<>();

    public enum ProjectStatus {
        ABERTO,//PLANNED
        ANALISE,//IN_PROGRESS
        COMPLETO,//COMPLETED
        CANCELADO//CANCELLED
    }
}
