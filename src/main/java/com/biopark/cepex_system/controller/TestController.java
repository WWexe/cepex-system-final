package com.biopark.cepex_system.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Olá, mundo autenticado!");
    }

    @GetMapping("/admin")
    // @PreAuthorize("hasRole('ADMIN')") // Se quiser testar autorização por role
    public ResponseEntity<String> adminHello() {
        return ResponseEntity.ok("Olá, Admin!");
    }
}