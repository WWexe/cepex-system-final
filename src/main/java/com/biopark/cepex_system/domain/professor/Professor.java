package com.biopark.cepex_system.domain.professor;


import com.biopark.cepex_system.domain.course.Discipline;
import com.biopark.cepex_system.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "professor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O primeiro nome não pode estar em branco.")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "O último nome não pode estar em branco.")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "O email não pode estar em branco.")
    @Email(message = "O email deve ter um formato válido.")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "O RA não pode estar em branco.")
    @Column(name = "ra", nullable = false, unique = true)
    private String ra;

    @NotBlank(message = "O CPF não pode estar em branco.")
    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Column(name = "number", length = 20)
    private String number;

    @NotNull(message = "O status do professor não pode ser nulo.")
    @Column(name = "st_user", nullable = false)
    private Boolean active;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull(message = "O usuário não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "professor_discipline",
            joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "discipline_id")
    )
    private Set<Discipline> disciplines = new HashSet<>();
}
