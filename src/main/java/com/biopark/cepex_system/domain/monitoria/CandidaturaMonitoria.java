package com.biopark.cepex_system.domain.monitoria;

import com.biopark.cepex_system.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "candidatura_monitoria")
@Entity(name = "CandidaturaMonitoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CandidaturaMonitoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "A monitoria não pode ser nula.")
    @ManyToOne
    @JoinColumn(name = "monitoria_id", nullable = false)
    private Monitoria monitoria;

    @NotNull(message = "O aluno não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private User aluno; // Associa a candidatura a um User (que seria o aluno)

    @Enumerated(EnumType.STRING)
    private StatusCandidatura status; // PENDENTE, APROVADA, REJEITADA

    private LocalDateTime dataCandidatura; // Data e hora da candidatura

    // Construtor para facilitar a criação de uma nova candidatura
    public CandidaturaMonitoria(Monitoria monitoria, User aluno) {
        this.monitoria = monitoria;
        this.aluno = aluno;
        this.status = StatusCandidatura.PENDENTE; // Status inicial
        this.dataCandidatura = LocalDateTime.now();
    }
}