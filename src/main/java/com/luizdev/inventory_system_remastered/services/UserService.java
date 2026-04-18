package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.request.UserRequest;
import com.luizdev.inventory_system_remastered.dto.response.UserResponse;
import com.luizdev.inventory_system_remastered.entity.User;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.EmailAlreadyExistsException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserAlreadyInactiveException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserNotFoundException;
import com.luizdev.inventory_system_remastered.mapper.UserMapper;
import com.luizdev.inventory_system_remastered.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder; // 👈 adicionado

    public UserService(UserRepository repository,
                       PasswordEncoder passwordEncoder) { // 👈 adicionado
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        log.info("Criando usuário com email: {}", request.email());

        if (repository.existsByEmail(request.email())) {
            log.warn("Tentativa de cadastro com email já existente: {}", request.email());
            throw new EmailAlreadyExistsException("Email já cadastrado.");
        }

        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password())); // 👈 adicionado
        user = repository.save(user);

        log.info("Usuário criado com sucesso. ID: {}, Email: {}", user.getId(), user.getEmail());
        return UserMapper.toResponse(user);
    }

    public UserResponse getById(Long id) {
        log.info("Buscando usuário por ID: {}", id);

        User user = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado. ID: {}", id);
                    return new UserNotFoundException("Usuário não encontrado com id: " + id);
                });

        log.info("Usuário encontrado. ID: {}, Email: {}", user.getId(), user.getEmail());
        return UserMapper.toResponse(user);
    }

    public Page<UserResponse> getAll(Pageable pageable) {
        log.info("Buscando todos os usuários. Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<UserResponse> users = repository.findAll(pageable)
                .map(UserMapper::toResponse);

        log.info("Total de usuários encontrados: {}", users.getTotalElements());
        return users;
    }

    public Page<UserResponse> getAllActive(Pageable pageable) {
        log.info("Buscando usuários ativos. Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<UserResponse> users = repository.findByActiveTrueOrderByNameAsc(pageable)
                .map(UserMapper::toResponse);

        log.info("Total de usuários ativos encontrados: {}", users.getTotalElements());
        return users;
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        log.info("Atualizando usuário ID: {}", id);

        User user = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado para atualização. ID: {}", id);
                    return new UserNotFoundException("Usuário não encontrado com id: " + id);
                });

        if (!user.getEmail().equals(request.email()) && repository.existsByEmail(request.email())) {
            log.warn("Tentativa de atualizar para email já existente: {}", request.email());
            throw new EmailAlreadyExistsException("Email já cadastrado.");
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setPassword(passwordEncoder.encode(request.password())); // 👈 adicionado

        user = repository.save(user);

        log.info("Usuário atualizado com sucesso. ID: {}, Email: {}", user.getId(), user.getEmail());
        return UserMapper.toResponse(user);
    }

    @Transactional
    public void deactivate(Long id) {
        log.info("Desativando usuário ID: {}", id);

        User user = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado para desativação. ID: {}", id);
                    return new UserNotFoundException("Usuário não encontrado com id: " + id);
                });

        if (!user.getActive()) {
            log.warn("Usuário já está inativo. ID: {}", id);
            throw new UserAlreadyInactiveException("Usuário com o id: " + id + " já está inativo.");
        }

        user.setActive(false);
        repository.save(user);

        log.info("Usuário desativado com sucesso. ID: {}", id);
    }
}