package com.biopark.cepex_system.service;

import com.biopark.cepex_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service; // Adicionar anotação @Service

@Service // Adicionar esta anotação para que o Spring possa injetá-lo
public class AuthorizationService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    // Apenas uma implementação do método é necessária.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = repository.findByLogin(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with login: " + username);
        }
        return user;
    }
}