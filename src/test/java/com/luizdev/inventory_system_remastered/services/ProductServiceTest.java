package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.request.ProductRequest;
import com.luizdev.inventory_system_remastered.dto.response.ProductResponse;
import com.luizdev.inventory_system_remastered.entity.Product;
import com.luizdev.inventory_system_remastered.exceptions.productExceptions.ProductAlreadyInactiveException;
import com.luizdev.inventory_system_remastered.exceptions.productExceptions.ProductNotFoundException;
import com.luizdev.inventory_system_remastered.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 👈 habilita o Mockito
class ProductServiceTest {

    @Mock // 👈 simula o repository
    private ProductRepository repository;

    @InjectMocks // 👈 injeta o mock no service
    private ProductService service;

    private Product product;
    private ProductRequest request;

    @BeforeEach // 👈 executa antes de cada teste
    void setUp() {
        // Prepara os dados que serão usados nos testes
        product = new Product();
        product.setId(1L);
        product.setName("Arroz");
        product.setDescription("Arroz tipo 1");
        product.setBrand("Tio João");
        product.setPrice(new BigDecimal("25.90"));
        product.setActive(true);

        request = new ProductRequest(
                "Arroz",
                "Arroz tipo 1",
                "Tio João",
                new BigDecimal("25.90")
        );
    }

    // ✅ Testa criação com sucesso
    @Test
    @DisplayName("Deve criar produto com sucesso")
    void create_success() {
        // Arrange
        when(repository.save(any(Product.class)))
                .thenReturn(product);

        // Act
        ProductResponse response = service.create(request);

        // Assert
        assertNotNull(response);
        assertEquals("Arroz", response.name());
        assertEquals("Tio João", response.brand());
        assertEquals(new BigDecimal("25.90"), response.price());
        assertTrue(response.active());

        // Verifica se o save foi chamado 1 vez
        verify(repository, times(1)).save(any(Product.class));
    }

    // ✅ Testa busca por ID com sucesso
    @Test
    @DisplayName("Deve retornar produto por ID com sucesso")
    void getById_success() {
        // Arrange
        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        // Act
        ProductResponse response = service.getById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Arroz", response.name());

        verify(repository, times(1)).findById(1L);
    }

    // ✅ Testa busca por ID não encontrado
    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado")
    void getById_notFound() {
        // Arrange
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            service.getById(99L);
        });

        verify(repository, times(1)).findById(99L);
    }

    // ✅ Testa atualização com sucesso
    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void update_success() {
        // Arrange
        ProductRequest updateRequest = new ProductRequest(
                "Arroz Integral",
                "Arroz integral tipo 1",
                "Tio João",
                new BigDecimal("30.90")
        );

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));
        when(repository.save(any(Product.class)))
                .thenReturn(product);

        // Act
        ProductResponse response = service.update(1L, updateRequest);

        // Assert
        assertNotNull(response);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Product.class));
    }

    // ✅ Testa desativação com sucesso
    @Test
    @DisplayName("Deve desativar produto com sucesso")
    void deactivate_success() {
        // Arrange
        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        // Act
        service.deactivate(1L);

        // Assert
        assertFalse(product.isActive());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Product.class));
    }

    // ✅ Testa desativação de produto já inativo
    @Test
    @DisplayName("Deve lançar exceção quando produto já está inativo")
    void deactivate_alreadyInactive() {
        // Arrange
        product.setActive(false);

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(ProductAlreadyInactiveException.class, () -> {
            service.deactivate(1L);
        });

        verify(repository, never()).save(any(Product.class));
    }

    // ✅ Testa atualização de produto não encontrado
    @Test
    @DisplayName("Deve lançar exceção ao atualizar produto não encontrado")
    void update_notFound() {
        // Arrange
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            service.update(99L, request);
        });

        verify(repository, never()).save(any(Product.class));
    }
}