/**
 * Este DTO (Data Transfer Object) serve como a "ponte" de dados entre
 * o seu front-end React e o seu back-end Spring Boot. Ele foi desenhado
 * para ser compatível com a nova arquitetura do seu projeto.
 */
package com.biopark.cepex_system.domain.usermanager;

import com.biopark.cepex_system.domain.user.UserRole ;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

// A anotação abaixo garante que campos nulos não serão enviados no JSON.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private UUID id;
    private String name;
    private String firstname;
    private String lastname;
    private String login;
    private String email;
    private String cpf;
    private String ra;
    private String password;
    private String tipo;
    private UserRole role;
    private boolean status;

    // Construtores
    public UserDTO() {
    }

    // Construtor completo para flexibilidade
    public UserDTO(UUID id, String name, String firstname, String lastname, String login, String email, String cpf, String ra, String password, String tipo, UserRole role, boolean status) {
        this.id = id;
        this.name = name;
        this.firstname = firstname;
        this.lastname = lastname;
        this.login = login;
        this.email = email;
        this.cpf = cpf;
        this.ra = ra;
        this.password = password;
        this.tipo = tipo;
        this.role = role;
        this.status = status;
    }


    // --- Getters e Setters Corrigidos e Completos ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
