package com.biopark.cepex_system.repository;

import com.biopark.cepex_system.domain.user.User;
import com.biopark.cepex_system.domain.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {
    UserDetails findByLogin(String login);

    User findByEmail(String email);

    // Contar usuários por role
    long countByRole(UserRole role);

    // Contar usuários ativos (status = true)
    long countByStatus(boolean status);
}