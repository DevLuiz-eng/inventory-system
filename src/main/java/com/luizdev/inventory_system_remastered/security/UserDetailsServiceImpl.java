package com.luizdev.inventory_system_remastered.security;

import com.luizdev.inventory_system_remastered.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Buscando usuário por email: {}", email);

        com.luizdev.inventory_system_remastered.entity.User user = userRepository
                .findByEmailAndActiveTrue(email)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado ou inativo: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado: " + email);
                });

        log.info("Usuário encontrado: {}", email);

        return new User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}