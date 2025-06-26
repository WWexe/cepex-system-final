package dev.cepex.Cepex.service;

import dev.cepex.Cepex.Model.Perfil;
import dev.cepex.Cepex.Model.RoleName; // Importe o seu Enum
import dev.cepex.Cepex.Repository.PerfilRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class PerfilService {

    private final PerfilRepository perfilRepository;

    @Autowired
    public PerfilService(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    @Transactional
    public Perfil salvarPerfil(Perfil perfil) {
        // CORRIGIDO: Checa se o enum 'nome' é nulo, em vez de usar hasText.
        if (perfil == null || perfil.getNome() == null) {
            throw new IllegalArgumentException("O nome do perfil não pode ser nulo.");
        }

        if (perfil.getId() == null) {
            Optional<Perfil> perfilExistente = perfilRepository.findByNome(perfil.getNome());
            if (perfilExistente.isPresent()) {
                throw new IllegalArgumentException("Já existe um perfil com o nome: " + perfil.getNome());
            }
        } else {
            Optional<Perfil> perfilComMesmoNome = perfilRepository.findByNome(perfil.getNome());
            if (perfilComMesmoNome.isPresent() && !perfilComMesmoNome.get().getId().equals(perfil.getId())) {
                throw new IllegalArgumentException("O nome '" + perfil.getNome() + "' já está em uso por outro perfil.");
            }
            if (!perfilRepository.existsById(perfil.getId())) {
                throw new EntityNotFoundException("Perfil com ID " + perfil.getId() + " não encontrado para atualização.");
            }
        }
        return perfilRepository.save(perfil);
    }

    public List<Perfil> listarTodosPerfis() {
        return perfilRepository.findAll();
    }

    public Optional<Perfil> buscarPerfilPorId(Integer id) {
        return perfilRepository.findById(id);
    }

    // CORRIGIDO: O método agora converte a String para o Enum correspondente.
    public Optional<Perfil> buscarPerfilPorNome(String nome) {
        if (!StringUtils.hasText(nome)) {
            return Optional.empty();
        }
        try {
            // Converte a String para o Enum (ex: "ROLE_ALUNO" -> RoleName.ROLE_ALUNO)
            RoleName roleName = RoleName.valueOf(nome.toUpperCase());
            return perfilRepository.findByNome(roleName);
        } catch (IllegalArgumentException e) {
            // Retorna vazio se a String não for um nome de Enum válido.
            return Optional.empty();
        }
    }

    @Transactional
    public void excluirPerfil(Integer id) {
        if (!perfilRepository.existsById(id)) {
            throw new EntityNotFoundException("Perfil com ID " + id + " não encontrado para exclusão.");
        }
        perfilRepository.deleteById(id);
    }

    @Transactional
    public Perfil atualizarPerfil(Integer id, Perfil perfilDetalhes) {
        Perfil perfilExistente = perfilRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Perfil com ID " + id + " não encontrado para atualização."));

        // CORRIGIDO: Checa se o enum 'nome' é nulo.
        if (perfilDetalhes == null || perfilDetalhes.getNome() == null) {
            throw new IllegalArgumentException("O nome do perfil não pode ser nulo para atualização.");
        }

        // CORRIGIDO: Compara enums com '!=' ou '.equals()'.
        if (perfilExistente.getNome() != perfilDetalhes.getNome()) {
            Optional<Perfil> conflitoDeNome = perfilRepository.findByNome(perfilDetalhes.getNome());
            if (conflitoDeNome.isPresent() && !conflitoDeNome.get().getId().equals(id)) {
                throw new IllegalArgumentException("O nome '" + perfilDetalhes.getNome() + "' já está em uso por outro perfil.");
            }
            perfilExistente.setNome(perfilDetalhes.getNome());
        }

        return perfilRepository.save(perfilExistente);
    }
}
