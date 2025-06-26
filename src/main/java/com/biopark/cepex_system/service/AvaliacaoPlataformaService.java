package com.biopark.cepex_system.service;

import com.biopark.cepex_system.domain.feedback.AvaliacaoPlataforma;
import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.repository.AvaliacaoPlataformaRepository;
import com.biopark.cepex_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AvaliacaoPlataformaService {

    private final AvaliacaoPlataformaRepository repository;
    private final UserRepository userRepository; // Para buscar o usuário que avaliou

    public AvaliacaoPlataformaService(AvaliacaoPlataformaRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AvaliacaoPlataforma save(Integer rating, UUID userId) {
        User user = (User) userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para a avaliação."));

        AvaliacaoPlataforma avaliacao = new AvaliacaoPlataforma(rating, user);
        return repository.save(avaliacao);
    }

    public List<AvaliacaoPlataforma> findAll() {
        return repository.findAll();
    }

    public Optional<AvaliacaoPlataforma> findById(UUID id) {
        return repository.findById(id);
    }

    // calcular a média das avaliações ou outras estatísticas
    public Double getAverageRating() {
        return repository.findAll().stream()
                .mapToInt(AvaliacaoPlataforma::getRating)
                .average()
                .orElse(0.0);
    }
}