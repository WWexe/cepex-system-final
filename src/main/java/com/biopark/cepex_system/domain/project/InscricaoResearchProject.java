package com.biopark.cepex_system.domain.project;

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

/**
 * Entidade que representa a inscrição de um aluno em um Projeto de Pesquisa.
 * Esta é uma *nova* entidade, criada para substituir a inscrição genérica de 'Projeto'.
 * Ela estabelece um relacionamento direto com 'ResearchProject' e 'User' (o aluno).
 */
@Table(name = "inscricao_research_project") // Nome da tabela específica para esta inscrição
@Entity(name = "InscricaoResearchProject")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class InscricaoResearchProject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relacionamento muitos-para-um com ResearchProject: Muitas inscrições para um projeto.
    // A coluna 'research_project_id' na tabela será a chave estrangeira.
    @NotNull(message = "O projeto de pesquisa não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "research_project_id", nullable = false)
    private ResearchProject researchProject;

    // Relacionamento muitos-para-um com User: Muitos alunos podem ter inscrições.
    // A coluna 'aluno_id' na tabela será a chave estrangeira.
    @NotNull(message = "O aluno não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private User aluno;

    // O status da inscrição, mapeado como String no banco de dados (PENDENTE, APROVADA, REJEITADA, CANCELADA).
    @Enumerated(EnumType.STRING)
    private StatusInscricaoProjeto status;

    private LocalDateTime dataInscricao; // Data e hora em que a inscrição foi realizada.

    // Construtor importante para a criação de novas inscrições com status inicial PENDENTE.
    public InscricaoResearchProject(ResearchProject researchProject, User aluno) {
        this.researchProject = researchProject;
        this.aluno = aluno;
        this.status = StatusInscricaoProjeto.PENDENTE; // Define o status inicial
        this.dataInscricao = LocalDateTime.now(); // Registra a data/hora atual
    }
}