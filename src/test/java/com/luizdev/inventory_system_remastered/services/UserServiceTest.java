package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.request.UserRequest;
import com.luizdev.inventory_system_remastered.dto.response.UserResponse;
import com.luizdev.inventory_system_remastered.entity.User;
import com.luizdev.inventory_system_remastered.enums.UserRole;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.EmailAlreadyExistsException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserAlreadyInactiveException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserNotFoundException;
import com.luizdev.inventory_system_remastered.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    private User user;
    private UserRequest request;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("João Silva");
        user.setEmail("joao@email.com");
        user.setPassword("123456");
        user.setRole(UserRole.ADMIN);
        user.setActive(true);

        request = new UserRequest(
                "João Silva",
                "joao@email.com",
                "123456",
                UserRole.ADMIN
        );

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void create_success() {
        when(repository.existsByEmail(request.email())).thenReturn(false);
        when(repository.save(any(User.class))).thenReturn(user);

        UserResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("João Silva", response.name());
        assertEquals("joao@email.com", response.email());
        assertEquals(UserRole.ADMIN, response.role());
        assertTrue(response.active());

        verify(repository, times(1)).existsByEmail(request.email());
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void create_emailAlreadyExists() {
        when(repository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            service.create(request);
        });

        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar usuário por ID com sucesso")
    void getById_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = service.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("João Silva", response.name());

        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void getById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            service.getById(99L);
        });

        verify(repository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve retornar todos os usuários paginado")
    void getAll_success() {
        Page<User> page = new PageImpl<>(List.of(user));
        when(repository.findAll(pageable)).thenReturn(page);

        Page<UserResponse> response = service.getAll(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar usuários ativos paginado")
    void getAllActive_success() {
        Page<User> page = new PageImpl<>(List.of(user));
        when(repository.findByActiveTrueOrderByNameAsc(pageable)).thenReturn(page);

        Page<UserResponse> response = service.getAllActive(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());

        verify(repository, times(1)).findByActiveTrueOrderByNameAsc(pageable);
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void update_success() {
        UserRequest updateRequest = new UserRequest(
                "João Atualizado",
                "joao@email.com",
                "123456",
                UserRole.USER
        );

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenReturn(user);

        UserResponse response = service.update(1L, updateRequest);

        assertNotNull(response);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com email já existente")
    void update_emailAlreadyExists() {
        UserRequest updateRequest = new UserRequest(
                "João Silva",
                "outro@email.com",
                "123456",
                UserRole.ADMIN
        );

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.existsByEmail("outro@email.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            service.update(1L, updateRequest);
        });

        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve desativar usuário com sucesso")
    void deactivate_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        service.deactivate(1L);

        assertFalse(user.getActive());
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário já está inativo")
    void deactivate_alreadyInactive() {
        user.setActive(false);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyInactiveException.class, () -> {
            service.deactivate(1L);
        });

        verify(repository, never()).save(any(User.class));
    }
}