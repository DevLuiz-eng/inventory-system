package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.request.StockMovementRequest;
import com.luizdev.inventory_system_remastered.dto.response.StockMovementResponse;
import com.luizdev.inventory_system_remastered.entity.*;
import com.luizdev.inventory_system_remastered.enums.TypeOfStockMovement;
import com.luizdev.inventory_system_remastered.enums.UserRole;
import com.luizdev.inventory_system_remastered.exceptions.inventoryExceptions.InsufficientStockException;
import com.luizdev.inventory_system_remastered.exceptions.productExceptions.ProductNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions.WarehouseNotFoundException;
import com.luizdev.inventory_system_remastered.repositories.*;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private StockMovementService service;

    private Product product;
    private Warehouse warehouse;
    private User user;
    private Inventory inventory;
    private StockMovementRequest request;
    private StockMovement movement;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Arroz");
        product.setBrand("Tio João");
        product.setPrice(new BigDecimal("25.90"));
        product.setActive(true);

        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Loja A");
        warehouse.setLocation("Rua das Flores, 123");
        warehouse.setActive(true);

        user = new User();
        user.setId(1L);
        user.setName("João Silva");
        user.setEmail("joao@email.com");
        user.setRole(UserRole.ADMIN);
        user.setActive(true);

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setQuantity(100);

        request = new StockMovementRequest(
                1L,
                1L,
                1L,
                TypeOfStockMovement.ENTRY,
                50,
                "Reposição de estoque"
        );

        movement = new StockMovement();
        movement.setId(1L);
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setCreatedBy(user);
        movement.setType(TypeOfStockMovement.ENTRY);
        movement.setQuantity(50);
        movement.setReason("Reposição de estoque");

        pageable = PageRequest.of(0, 10);
    }

    // ✅ Entrada de estoque com sucesso
    @Test
    @DisplayName("Deve registrar entrada de estoque com sucesso")
    void entry_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));
        when(stockMovementRepository.save(any(StockMovement.class)))
                .thenReturn(movement);

        StockMovementResponse response = service.entry(request);

        assertNotNull(response);
        assertEquals(TypeOfStockMovement.ENTRY, response.type());
        assertEquals(50, response.quantity());

        // Verifica se o estoque foi somado
        assertEquals(150, inventory.getQuantity());

        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verify(stockMovementRepository, times(1)).save(any(StockMovement.class));
    }

    // ✅ Entrada cria inventário novo se não existir
    @Test
    @DisplayName("Deve criar inventário novo quando não existir")
    void entry_createsNewInventory() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(movement);

        StockMovementResponse response = service.entry(request);

        assertNotNull(response);
        verify(inventoryRepository, times(2)).save(any(Inventory.class));
    }

    // ✅ Entrada com produto não encontrado
    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado na entrada")
    void entry_productNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        StockMovementRequest invalidRequest = new StockMovementRequest(
                99L, 1L, 1L,
                TypeOfStockMovement.ENTRY,
                50, "Reposição"
        );

        assertThrows(ProductNotFoundException.class, () -> {
            service.entry(invalidRequest);
        });

        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }

    // ✅ Entrada com armazém não encontrado
    @Test
    @DisplayName("Deve lançar exceção quando armazém não encontrado na entrada")
    void entry_warehouseNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());

        StockMovementRequest invalidRequest = new StockMovementRequest(
                1L, 99L, 1L,
                TypeOfStockMovement.ENTRY,
                50, "Reposição"
        );

        assertThrows(WarehouseNotFoundException.class, () -> {
            service.entry(invalidRequest);
        });

        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }

    // ✅ Entrada com usuário não encontrado
    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado na entrada")
    void entry_userNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        StockMovementRequest invalidRequest = new StockMovementRequest(
                1L, 1L, 99L,
                TypeOfStockMovement.ENTRY,
                50, "Reposição"
        );

        assertThrows(UserNotFoundException.class, () -> {
            service.entry(invalidRequest);
        });

        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }

    // ✅ Saída de estoque com sucesso
    @Test
    @DisplayName("Deve registrar saída de estoque com sucesso")
    void exit_success() {
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));

        service.exit(product, warehouse, 50, "Venda #1", user);

        // Verifica se o estoque foi subtraído
        assertEquals(50, inventory.getQuantity());

        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verify(stockMovementRepository, times(1)).save(any(StockMovement.class));
    }

    // ✅ Saída com estoque insuficiente
    @Test
    @DisplayName("Deve lançar exceção quando estoque insuficiente")
    void exit_insufficientStock() {
        inventory.setQuantity(10);

        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));

        assertThrows(InsufficientStockException.class, () -> {
            service.exit(product, warehouse, 50, "Venda #1", user);
        });

        verify(inventoryRepository, never()).save(any(Inventory.class));
        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }

    // ✅ Saída sem inventário cadastrado
    @Test
    @DisplayName("Deve lançar exceção quando inventário não encontrado na saída")
    void exit_inventoryNotFound() {
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(InsufficientStockException.class, () -> {
            service.exit(product, warehouse, 50, "Venda #1", user);
        });

        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }

    // ✅ Busca por produto
    @Test
    @DisplayName("Deve retornar movimentações por produto")
    void getByProductId_success() {
        Page<StockMovement> page = new PageImpl<>(List.of(movement));
        when(productRepository.existsById(1L)).thenReturn(true);
        when(stockMovementRepository.findByProductId(1L, pageable)).thenReturn(page);

        Page<StockMovementResponse> response = service.getByProductId(1L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    // ✅ Busca por produto não encontrado
    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado ao buscar movimentações")
    void getByProductId_productNotFound() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> {
            service.getByProductId(99L, pageable);
        });
    }

    // ✅ Busca por armazém
    @Test
    @DisplayName("Deve retornar movimentações por armazém")
    void getByWarehouseId_success() {
        Page<StockMovement> page = new PageImpl<>(List.of(movement));
        when(warehouseRepository.existsById(1L)).thenReturn(true);
        when(stockMovementRepository.findByWarehouseId(1L, pageable)).thenReturn(page);

        Page<StockMovementResponse> response = service.getByWarehouseId(1L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    // ✅ Busca por armazém não encontrado
    @Test
    @DisplayName("Deve lançar exceção quando armazém não encontrado ao buscar movimentações")
    void getByWarehouseId_warehouseNotFound() {
        when(warehouseRepository.existsById(99L)).thenReturn(false);

        assertThrows(WarehouseNotFoundException.class, () -> {
            service.getByWarehouseId(99L, pageable);
        });
    }
}