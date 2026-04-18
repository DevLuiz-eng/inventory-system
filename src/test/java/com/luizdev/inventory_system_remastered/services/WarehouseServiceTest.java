package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.request.WarehouseRequest;
import com.luizdev.inventory_system_remastered.dto.response.WarehouseResponse;
import com.luizdev.inventory_system_remastered.entity.Warehouse;
import com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions.WarehouseAlreadyInactiveException;
import com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions.WarehouseNotFoundException;
import com.luizdev.inventory_system_remastered.repositories.WarehouseRepository;
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
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository repository;

    @InjectMocks
    private WarehouseService service;

    private Warehouse warehouse;
    private WarehouseRequest request;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Loja A");
        warehouse.setLocation("Rua das Flores, 123");
        warehouse.setActive(true);

        request = new WarehouseRequest("Loja A", "Rua das Flores, 123");
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve criar armazém com sucesso")
    void create_success() {
        when(repository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("Loja A", response.name());
        assertEquals("Rua das Flores, 123", response.location());
        assertTrue(response.active());

        verify(repository, times(1)).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Deve retornar armazém por ID com sucesso")
    void getById_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(warehouse));

        WarehouseResponse response = service.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Loja A", response.name());

        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando armazém não encontrado")
    void getById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WarehouseNotFoundException.class, () -> {
            service.getById(99L);
        });

        verify(repository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve retornar todos os armazéns paginado")
    void getAll_success() {
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse));
        when(repository.findAll(pageable)).thenReturn(page);

        Page<WarehouseResponse> response = service.getAll(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Loja A", response.getContent().get(0).name());

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar armazéns ativos paginado")
    void getAllActive_success() {
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse));
        when(repository.findByActiveTrueOrderByNameAsc(pageable)).thenReturn(page);

        Page<WarehouseResponse> response = service.getAllActive(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertTrue(response.getContent().get(0).active());

        verify(repository, times(1)).findByActiveTrueOrderByNameAsc(pageable);
    }

    @Test
    @DisplayName("Deve atualizar armazém com sucesso")
    void update_success() {
        WarehouseRequest updateRequest = new WarehouseRequest(
                "Loja B",
                "Rua das Rosas, 456"
        );

        when(repository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(repository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseResponse response = service.update(1L, updateRequest);

        assertNotNull(response);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar armazém não encontrado")
    void update_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WarehouseNotFoundException.class, () -> {
            service.update(99L, request);
        });

        verify(repository, never()).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Deve desativar armazém com sucesso")
    void deactivate_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(warehouse));

        service.deactivate(1L);

        assertFalse(warehouse.getActive());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando armazém já está inativo")
    void deactivate_alreadyInactive() {
        warehouse.setActive(false);
        when(repository.findById(1L)).thenReturn(Optional.of(warehouse));

        assertThrows(WarehouseAlreadyInactiveException.class, () -> {
            service.deactivate(1L);
        });

        verify(repository, never()).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao desativar armazém não encontrado")
    void deactivate_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WarehouseNotFoundException.class, () -> {
            service.deactivate(99L);
        });

        verify(repository, never()).save(any(Warehouse.class));
    }
}