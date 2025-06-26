package com.biopark.cepex_system.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.biopark.cepex_system.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    private static final String ISSUER = "cepex-auth-api"; // Nome do emissor do token

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getLogin()) // Identificador do usuário no token
                    .withClaim("userId", user.getId().toString()) // Adicionando o ID do usuário como uma claim
                    .withClaim("role", user.getRole().toString()) // Adicionando o role como uma claim
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token) // Verifica a assinatura e a expiração
                    .getSubject(); // Retorna o 'subject' (login do usuário) se o token for válido
        } catch (JWTVerificationException exception) {
            // Token inválido (expirado, assinatura incorreta, etc.)
            return ""; // Retorna string vazia ou lança uma exceção específica
        }
    }

    private Instant genExpirationDate() {
        // Define o token para expirar em 2 horas (ajuste conforme necessário)
        // Usando ZoneOffset.of("-03:00") para o fuso horário de Brasília (BRT)
        return LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-03:00"));
    }
}