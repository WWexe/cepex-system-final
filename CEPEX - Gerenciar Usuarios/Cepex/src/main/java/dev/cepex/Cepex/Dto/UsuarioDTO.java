package dev.cepex.Cepex.Dto; // Verifique se o pacote está correto

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;

/**
 * Data Transfer Object (DTO) para representar um usuário de forma unificada para o front-end.
 * Combina campos de Aluno e Professor.
 * A anotação @JsonInclude(JsonInclude.Include.NON_NULL) garante que, ao converter para JSON,
 * campos com valor nulo (como 'curso' para um professor) não serão enviados.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {

    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String ra;
    private String cpf;
    private String senha;
    private String tipo; // "Aluno" ou "Professor"
    private String curso;
    private boolean coordenador;
    private Set<PerfilDTO> perfis;

    // --- GETTERS E SETTERS ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public boolean isCoordenador() {
        return coordenador;
    }

    public void setCoordenador(boolean coordenador) {
        this.coordenador = coordenador;
    }

    public Set<PerfilDTO> getPerfis() {
        return perfis;
    }

    public void setPerfis(Set<PerfilDTO> perfis) {
        this.perfis = perfis;
    }
}