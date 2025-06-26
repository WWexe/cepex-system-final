package dev.cepex.Cepex.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "student")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Integer id;

    @Column(name = "first_name")
    private String firstname;

    @Column(name = "last_name")
    private String lastname;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "ra", unique = true)
    private String ra;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    // --- CORREÇÃO FINAL AQUI ---
    // Adicionado o campo para mapear a coluna 'st_user' do banco de dados.
    @Column(name = "st_user", columnDefinition = "TINYINT")
    private boolean stUser;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_user_id", referencedColumnName = "user_id")
    private ContaUsuario user;

    // Getters e Setters para todos os campos
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getRa() { return ra; }
    public void setRa(String ra) { this.ra = ra; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdateAt() { return updateAt; }
    public void setUpdateAt(LocalDateTime updateAt) { this.updateAt = updateAt; }

    // Getter e Setter para o novo campo stUser
    public boolean isStUser() { return stUser; }
    public void setStUser(boolean stUser) { this.stUser = stUser; }

    public ContaUsuario getUser() { return user; }
    public void setUser(ContaUsuario user) { this.user = user; }
}
