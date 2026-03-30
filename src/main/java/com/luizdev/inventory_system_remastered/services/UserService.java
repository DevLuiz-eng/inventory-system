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
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {

        log.info("criando usuário com email: {}", request.email());

        if (repository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email já cadastrado.");
        }

        User user = UserMapper.toEntity(request);
        user = repository.save(user);

        log.info("Usuário criado com id: {}", user.getId());
        return UserMapper.toResponse(user);

    }


    public UserResponse getById(Long id) {
        User user = repository.findById(id).
                orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com id: " + id)
                );

        return UserMapper.toResponse(user);
    }

    public List<UserResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toResponse).
                toList();
    }

    public List<UserResponse> getAllActive() {
        return repository.findByActiveTrueOrderByNameAsc().
                stream().
                map(UserMapper::toResponse).toList();
    }

    @Transactional
    public void deactivate(Long id) {
        User user = repository.findById(id).
                orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com id:" + id));

        if (!user.getActive()){
            throw new UserAlreadyInactiveException("Usuário com o id: " + id + " já está inativo.");
        }

        log.info("Usuário com o id: " + id + " foi desativado.");

        user.setActive(false);
    }
}
