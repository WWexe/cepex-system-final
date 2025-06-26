package com.biopark.cepex_system.domain.student;


import com.biopark.cepex_system.domain.course.Course;
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
import java.util.UUID;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O nome do aluno não pode estar em branco.")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "O sobrenome do aluno não pode estar em branco.")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "O email não pode estar em branco.")
    @Email(message = "O email deve ter um formato válido.")
    @Column(name = "email", nullable = false, unique = true,  length = 60)
    private String email;

    @Column(name = "number", length = 20)
    private String number;

    @NotBlank(message = "O RA não pode estar em branco.")
    @Column(name = "RA",  nullable = false, unique = true, length = 8)
    private String ra;

    @NotBlank(message = "O CPF não pode estar em branco.")
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    @NotNull(message = "O status do aluno não pode ser nulo.")
    @Column(name = "st_user",  nullable = false)
    private Boolean status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull(message = "O usuário não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull(message = "O curso não pode ser nulo.")
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

}
