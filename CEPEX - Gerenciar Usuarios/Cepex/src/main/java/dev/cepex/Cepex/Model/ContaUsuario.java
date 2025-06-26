package dev.cepex.Cepex.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp; // Importa a nova anotação
import java.time.LocalDateTime;

/**
 * A entidade de login, agora com campos de timestamp automáticos.
 * O tipo do ID foi alterado para Long para corresponder ao tipo BIGINT
 * da coluna 'user_id' no banco de dados.
 */
@Entity
@Table(name = "user")
public class ContaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // CORRIGIDO: de Integer para Long para corresponder ao BIGINT

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "st_user", columnDefinition = "TINYINT")
    private boolean stUser;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- CORREÇÃO AQUI ---
    // A anotação @UpdateTimestamp diz ao Hibernate para atualizar este campo
    // com a data e hora atuais sempre que o registo for modificado.
    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    // Getters e Setters atualizados
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean isStUser() { return stUser; }
    public void setStUser(boolean stUser) { this.stUser = stUser; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdateAt() { return updateAt; }
    public void setUpdateAt(LocalDateTime updateAt) { this.updateAt = updateAt; }
}
