package com.biopark.cepex_system.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurations {
    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        // Swagger/OpenAPI endpoints - Permitir acesso público
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**", "/swagger-resources/**").permitAll()

                        // Permitir que qualquer usuário autenticado envie feedback
                        .requestMatchers(HttpMethod.POST, "/feedback").authenticated()
                        // Permitir acesso público ou autenticado para a média de avaliações, dependendo da sua necessidade
                        .requestMatchers(HttpMethod.GET, "/feedback/average-rating").permitAll()
                        // Proteger listagem de todas as avaliações
                        .requestMatchers(HttpMethod.GET, "/feedback").hasRole("ADMIN")

                        // Rotas Públicas (Login, Registro e Recuperação de Senha)
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/forgot-password").permitAll() // <-- ADICIONE ESTA LINHA
                        .requestMatchers(HttpMethod.POST, "/auth/reset-password").permitAll()   // <-- E ESTA LINHA
                        .requestMatchers(HttpMethod.GET, "/api/test/hello").permitAll()

                        // Rotas protegidas por Papel:

                        // ADMIN pode gerenciar usuários (CRUD completo)
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        // ADMIN pode acessar estatísticas gerais (ex: do Dashboard)
                        .requestMatchers(HttpMethod.GET, "/monitorias/statistics").hasRole("ADMIN")

                        // Professores podem criar e gerenciar suas monitorias
                        .requestMatchers(HttpMethod.POST, "/monitorias").hasAnyRole("ADMIN", "PROFESSOR")
                        .requestMatchers(HttpMethod.PUT, "/monitorias/{id}").hasAnyRole("ADMIN", "PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/monitorias/{id}").hasAnyRole("ADMIN", "PROFESSOR")
                        // Monitorias: Alunos podem candidatar-se e verificar status (mas não criar/deletar)
                        .requestMatchers(HttpMethod.POST, "/monitorias/{monitoriaId}/candidatar").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/monitorias/{monitoriaId}/candidatar").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(HttpMethod.GET, "/monitorias/{monitoriaId}/candidatura-status").hasAnyRole("ADMIN", "STUDENT", "PROFESSOR", "COORDENATION")


                        // New Research Project Routes
                        .requestMatchers(HttpMethod.POST, "/research-projects").hasAnyRole("ADMIN", "PROFESSOR")
                        .requestMatchers(HttpMethod.PUT, "/research-projects/{id}").hasAnyRole("ADMIN", "PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/research-projects/{id}").hasAnyRole("ADMIN", "PROFESSOR")
                        .requestMatchers(HttpMethod.POST, "/research-projects/{projectId}/inscrever").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/research-projects/{projectId}/inscrever").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(HttpMethod.GET, "/research-projects/{projectId}/inscricao-status").hasAnyRole("ADMIN", "STUDENT", "PROFESSOR", "COORDENATION")


                        // New Extension Project Routes
                        .requestMatchers(HttpMethod.POST, "/extension-projects").hasAnyRole("ADMIN", "PROFESSOR", "COORDENATION", "SECRETARY")
                        .requestMatchers(HttpMethod.PUT, "/extension-projects/{id}").hasAnyRole("ADMIN", "PROFESSOR", "COORDENATION", "SECRETARY")
                        .requestMatchers(HttpMethod.DELETE, "/extension-projects/{id}").hasAnyRole("ADMIN", "COORDENATION", "SECRETARY")
                        .requestMatchers(HttpMethod.POST, "/extension-projects/{projectId}/inscrever").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/extension-projects/{projectId}/inscrever").hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(HttpMethod.GET, "/extension-projects/{projectId}/inscricao-status").hasAnyRole("ADMIN", "STUDENT", "PROFESSOR", "COORDENATION", "SECRETARY")
                        .requestMatchers(HttpMethod.GET, "/extension-projects/{projectId}/inscricoes").hasAnyRole("ADMIN", "PROFESSOR", "COORDENATION", "SECRETARY")


                        // Rotas para Módulo de Aprovações (para Coordenadores, Secretárias, Admin)
                        .requestMatchers("/approvals/**").hasAnyRole("ADMIN", "COORDENATION", "SECRETARY")


                        // Rotas para Professores, Cursos, Disciplinas (CRUD)
                        // Apenas ADMIN ou COORDENATION podem gerenciar estes
                        .requestMatchers("/professors/**").hasAnyRole("ADMIN", "COORDENATION")
                        .requestMatchers("/courses/**").hasAnyRole("ADMIN", "COORDENATION", "PROFESSOR")
                        .requestMatchers("/disciplines/**").hasAnyRole("ADMIN", "COORDENATION", "PROFESSOR")


                        // Todas as outras requisições exigem apenas que o usuário esteja autenticado
                        // e não foram explicitamente protegidas por papel acima
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001,", "http://localhost:8081,"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}