package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.response.InventoryResponse;
import com.luizdev.inventory_system_remastered.entity.Inventory;
import com.luizdev.inventory_system_remastered.entity.Product;
import com.luizdev.inventory_system_remastered.entity.Warehouse;
import com.luizdev.inventory_system_remastered.exceptions.inventoryExceptions.InventoryNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.productExceptions.ProductNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions.WarehouseNotFoundException;
import com.luizdev.inventory_system_remastered.repositories.InventoryRepository;
import com.luizdev.inventory_system_remastered.repositories.ProductRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private InventoryService service;

    private Product product;
    private Warehouse warehouse;
    private Inventory inventory;
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

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setQuantity(100);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve retornar inventário por produto com sucesso")
    void getByProductId_success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(inventoryRepository.findByProductId(1L)).thenReturn(List.of(inventory));

        List<InventoryResponse> response = service.getByProductId(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).productId());
        assertEquals("Arroz", response.get(0).productName());
        assertEquals(100, response.get(0).quantity());

        verify(productRepository, times(1)).existsById(1L);
        verify(inventoryRepository, times(1)).findByProductId(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado ao buscar inventário")
    void getByProductId_productNotFound() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> {
            service.getByProductId(99L);
        });

        verify(inventoryRepository, never()).findByProductId(any());
    }

    @Test
    @DisplayName("Deve retornar inventário por armazém com sucesso")
    void getByWarehouseId_success() {
        Page<Inventory> page = new PageImpl<>(List.of(inventory));
        when(warehouseRepository.existsById(1L)).thenReturn(true);
        when(inventoryRepository.findByWarehouseId(1L, pageable)).thenReturn(page);

        Page<InventoryResponse> response = service.getByWarehouseId(1L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Loja A", response.getContent().get(0).warehouseName());
        assertEquals(100, response.getContent().get(0).quantity());

        verify(warehouseRepository, times(1)).existsById(1L);
        verify(inventoryRepository, times(1)).findByWarehouseId(1L, pageable);
    }

    @Test
    @DisplayName("Deve lançar exceção quando armazém não encontrado ao buscar inventário")
    void getByWarehouseId_warehouseNotFound() {
        when(warehouseRepository.existsById(99L)).thenReturn(false);

        assertThrows(WarehouseNotFoundException.class, () -> {
            service.getByWarehouseId(99L, pageable);
        });

        verify(inventoryRepository, never()).findByWarehouseId(any(), any());
    }

    @Test
    @DisplayName("Deve retornar inventário por produto e armazém com sucesso")
    void getByProductAndWarehouse_success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(warehouseRepository.existsById(1L)).thenReturn(true);
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));

        InventoryResponse response = service.getByProductAndWarehouse(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.productId());
        assertEquals(1L, response.warehouseId());
        assertEquals(100, response.quantity());

        verify(productRepository, times(1)).existsById(1L);
        verify(warehouseRepository, times(1)).existsById(1L);
        verify(inventoryRepository, times(1)).findByProductIdAndWarehouseId(1L, 1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado ao buscar inventário por produto e armazém")
    void getByProductAndWarehouse_productNotFound() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> {
            service.getByProductAndWarehouse(99L, 1L);
        });

        verify(inventoryRepository, never()).findByProductIdAndWarehouseId(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando armazém não encontrado ao buscar inventário por produto e armazém")
    void getByProductAndWarehouse_warehouseNotFound() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(warehouseRepository.existsById(99L)).thenReturn(false);

        assertThrows(WarehouseNotFoundException.class, () -> {
            service.getByProductAndWarehouse(1L, 99L);
        });

        verify(inventoryRepository, never()).findByProductIdAndWarehouseId(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando inventário não encontrado")
    void getByProductAndWarehouse_inventoryNotFound() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(warehouseRepository.existsById(1L)).thenReturn(true);
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class, () -> {
            service.getByProductAndWarehouse(1L, 1L);
        });
    }
}