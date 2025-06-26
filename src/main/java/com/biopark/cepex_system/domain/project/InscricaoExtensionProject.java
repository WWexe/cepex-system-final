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
 * Entidade que representa a inscrição de um aluno em um Projeto de Extensão.
 * Similar a InscricaoResearchProject, esta é uma *nova* entidade, garantindo
 * a separação clara das inscrições por tipo de projeto.
 */
@Table(name = "inscricao_extension_project") // Nome da tabela específica para esta inscrição
@Entity(name = "InscricaoExtensionProject")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class InscricaoExtensionProject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relacionamento muitos-para-um com ExtensionProject.
    @NotNull(message = "O projeto de extensão não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "extension_project_id", nullable = false)
    private ExtensionProject extensionProject;

    // Relacionamento muitos-para-um com User.
    @NotNull(message = "O aluno não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private User aluno;

    // O status da inscrição, mapeado como String no banco de dados.
    @Enumerated(EnumType.STRING)
    private StatusInscricaoProjeto status;

    private LocalDateTime dataInscricao;

    // Construtor para a criação de novas inscrições com status inicial PENDENTE.
    public InscricaoExtensionProject(ExtensionProject extensionProject, User aluno) {
        this.extensionProject = extensionProject;
        this.aluno = aluno;
        this.status = StatusInscricaoProjeto.PENDENTE; // Define o status inicial
        this.dataInscricao = LocalDateTime.now(); // Registra a data/hora atual
    }
}