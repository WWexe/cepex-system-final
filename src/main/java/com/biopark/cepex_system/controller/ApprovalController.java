package com.biopark.cepex_system.controller;

import com.biopark.cepex_system.domain.approval.ApprovalItemDTO;
import com.biopark.cepex_system.service.ApprovalService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping; // Importar PutMapping
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // Importar RequestParam
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/approvals")
@CrossOrigin(origins = "*")
public class ApprovalController {

    private final ApprovalService service;

    public ApprovalController(ApprovalService service) {
        this.service = service;
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENATION', 'SECRETARY')")
    public ResponseEntity<List<ApprovalItemDTO>> getPendingApprovals() {
        List<ApprovalItemDTO> pendingApprovals = service.getPendingApprovals();
        return ResponseEntity.ok(pendingApprovals);
    }

    // Aprovar uma atividade
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENATION', 'SECRETARY')")
    public ResponseEntity<Void> approveItem(@PathVariable UUID id, @RequestParam @NotBlank(message = "O tipo não pode estar em branco.") String type) {
        try {
            service.approveItem(id, type);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            // Pode-se retornar 404 se não encontrado ou 400 se tipo inválido
            return ResponseEntity.badRequest().build();
        }
    }

    // Rejeitar uma atividade
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENATION', 'SECRETARY')")
    public ResponseEntity<Void> rejectItem(@PathVariable UUID id, @RequestParam @NotBlank(message = "O tipo não pode estar em branco.") String type) {
        try {
            service.rejectItem(id, type);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}