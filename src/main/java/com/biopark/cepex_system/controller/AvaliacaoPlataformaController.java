package com.biopark.cepex_system.controller;

import com.biopark.cepex_system.domain.feedback.AvaliacaoPlataforma;
import com.biopark.cepex_system.domain.user.User; // Importar User
import com.biopark.cepex_system.service.AvaliacaoPlataformaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Para obter o usuário autenticado
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/feedback")
@CrossOrigin(origins = "*")
public class AvaliacaoPlataformaController {

    private final AvaliacaoPlataformaService service;

    public AvaliacaoPlataformaController(AvaliacaoPlataformaService service) {
        this.service = service;
    }

    // enviar uma nova avaliação de plataforma
    @PostMapping
    public ResponseEntity<AvaliacaoPlataforma> sendFeedback(@RequestParam @NotNull @Min(1) @Max(5) Integer rating, @AuthenticationPrincipal User userAutenticado) {
        if (userAutenticado == null) {
            return ResponseEntity.status(401).build(); // Usuário não autenticado
        }

        try {
            AvaliacaoPlataforma avaliacao = service.save(rating, userAutenticado.getId());
            return ResponseEntity.ok(avaliacao);
        } catch (RuntimeException e) {
            // Logar a exceção e retornar um erro mais específico, se necessário
            return ResponseEntity.badRequest().body(null);
        }
    }

    // listar todas as avaliações (opcional, para um dashboard de admin/gestão)
    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')") // Proteger este endpoint para apenas admins
    public ResponseEntity<List<AvaliacaoPlataforma>> getAllFeedback() {
        List<AvaliacaoPlataforma> avaliacoes = service.findAll();
        return ResponseEntity.ok(avaliacoes);
    }

    // média das avaliações (útil para exibir no front-end)
    @GetMapping("/average-rating")
    public ResponseEntity<Double> getAverageRating() {
        Double average = service.getAverageRating();
        return ResponseEntity.ok(average);
    }
}