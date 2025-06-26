package com.biopark.cepex_system.domain.feedback;

import com.biopark.cepex_system.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "avaliacao_plataforma")
@Entity(name = "AvaliacaoPlataforma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AvaliacaoPlataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "A avaliação não pode ser nula.")
    @Column(name = "rating",  nullable = false)
    private Integer rating; // A avaliação em estrelas (ex: 1 a 5)

    @NotNull(message = "O usuário não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // O usuário que fez a avaliação

    @CreationTimestamp
    @Column(name = "avaliation_date")
    private LocalDateTime dataAvaliacao; // Data e hora da avaliação

    // Construtor para facilitar a criação de uma nova avaliação
    public AvaliacaoPlataforma(Integer rating, User user) {
        this.rating = rating;
        this.user = user;
        this.dataAvaliacao = LocalDateTime.now();
    }
}