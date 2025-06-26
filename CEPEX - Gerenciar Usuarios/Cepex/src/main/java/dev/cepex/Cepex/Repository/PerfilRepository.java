package dev.cepex.Cepex.Repository; // Ou seu pacote de repositório

import dev.cepex.Cepex.Model.Perfil;
import dev.cepex.Cepex.Model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Integer> {
    // Altere o tipo do parâmetro de String para RoleName
    Optional<Perfil> findByNome(RoleName nome);
}