package com.biopark.cepex_system.domain.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import java.util.*;

@Table(name = "users")
@Entity(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O login não pode estar em branco.")
    @Column(nullable = false, unique = true)
    private String login;

    @NotBlank(message = "A senha não pode estar em branco.")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "O email não pode estar em branco.")
    @Email(message = "O email deve ter um formato válido.")
    @Column(nullable = false, unique = true)
    private String email;


    @NotNull(message = "O status do usuário não pode ser nulo.")
    @Column(nullable = false)
    private boolean status;

    @NotNull(message = "O papel do usuário não pode ser nulo.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // Construtor para o processo de registro
    public User(String login, String email, String password, UserRole role) {
        this.login = login;
        this.email = email;
        this.password = password; // Senha já deve vir criptografada
        this.role = role;
        this.status = true; // Define o usuário como ativo por padrão no registro
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Mapeia UserRole para GrantedAuthority de forma mais granular
        // Exemplo: se UserRole.ADMIN -> ROLE_ADMIN, UserRole.PROFESSOR -> ROLE_PROFESSOR
        if (this.role == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else if (this.role == UserRole.PROFESSOR) {
            return List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"), new SimpleGrantedAuthority("ROLE_USER"));
        } else if (this.role == UserRole.STUDENT) {
            return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"), new SimpleGrantedAuthority("ROLE_USER"));
        } else { // COORDENATION, SECRETARY e outros futuros papéis
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }


    @Override
    @JsonIgnore
    public String getUsername() {
        return login;

    }@Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true; 
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.status;
    }
}