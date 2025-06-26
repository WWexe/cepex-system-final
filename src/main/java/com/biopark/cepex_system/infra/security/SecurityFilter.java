package com.biopark.cepex_system.infra.security;

import com.biopark.cepex_system.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marca como um componente Spring
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var token = this.recoverToken(request);

        if (token != null) {
            var login = tokenService.validateToken(token); // Valida o token e obtém o login
            if (login != null && !login.isEmpty()) {
                UserDetails user = userRepository.findByLogin(login);
                if (user != null) {
                    // Cria o objeto de autenticação
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    // Define o usuário como autenticado no contexto de segurança do Spring
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response); // Continua o fluxo da requisição
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", ""); // Remove o prefixo "Bearer "
    }
}