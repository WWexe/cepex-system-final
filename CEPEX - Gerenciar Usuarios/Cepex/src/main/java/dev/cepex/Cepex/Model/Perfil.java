package dev.cepex.Cepex.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles") // O nome da sua tabela no banco
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // --- CORREÇÃO PRINCIPAL ---
    // O campo agora é do tipo Enum para corresponder ao banco de dados.
    @Enumerated(EnumType.STRING)
    @Column(name = "name") // Garante que o nome da coluna é 'name'
    private RoleName nome;

    // --- CONSTRUTORES ---
    public Perfil() {
    }

    public Perfil(RoleName nome) {
        this.nome = nome;
    }

    // --- GETTERS E SETTERS CORRIGIDOS ---

    public RoleName getNome() {
        return this.nome;
    }

    public void setNome(RoleName nome) {
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
