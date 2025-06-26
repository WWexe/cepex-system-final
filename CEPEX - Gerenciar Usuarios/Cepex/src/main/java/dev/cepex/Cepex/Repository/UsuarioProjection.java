package dev.cepex.Cepex.Repository;

public interface UsuarioProjection {
    Long getId();
    String getFirstname();
    String getLastname();
    String getEmail();
    String getTipo(); // 'Aluno' ou 'Professor'
}