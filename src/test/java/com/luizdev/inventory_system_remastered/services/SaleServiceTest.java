package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.request.PaymentRequest;
import com.luizdev.inventory_system_remastered.dto.request.SaleItemRequest;
import com.luizdev.inventory_system_remastered.dto.request.SaleRequest;
import com.luizdev.inventory_system_remastered.dto.response.SaleResponse;
import com.luizdev.inventory_system_remastered.entity.*;
import com.luizdev.inventory_system_remastered.enums.PaymentMethod;
import com.luizdev.inventory_system_remastered.enums.UserRole;
import com.luizdev.inventory_system_remastered.exceptions.productExceptions.ProductNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.saleExceptions.SaleNotFoundException;
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
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SaleItemRepository saleItemRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StockMovementService stockMovementService;

    @InjectMocks
    private SaleService service;

    private Product product;
    private Warehouse warehouse;
    private User user;
    private Sale sale;
    private SaleRequest request;
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

        sale = new Sale();
        sale.setId(1L);
        sale.setWarehouse(warehouse);
        sale.setCreatedBy(user);
        sale.setTotalAmount(new BigDecimal("129.50"));
        sale.setSaleItems(List.of());
        sale.setPayments(List.of());

        request = new SaleRequest(
                1L,
                1L,
                List.of(new SaleItemRequest(1L, 5)),
                List.of(new PaymentRequest(
                        new BigDecimal("129.50"),
                        PaymentMethod.PIX,
                        null
                ))
        );

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve criar venda com sucesso")
    void create_success() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(saleItemRepository.saveAll(any())).thenReturn(List.of());
        when(paymentRepository.saveAll(any())).thenReturn(List.of());
        doNothing().when(stockMovementService).exit(any(), any(), any(), any(), any());

        SaleResponse response = service.create(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(1L, response.warehouseId());
        assertEquals(1L, response.userId());

        verify(warehouseRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(stockMovementService, times(1)).exit(any(), any(), any(), any(), any());
        verify(saleItemRepository, times(1)).saveAll(any());
        verify(paymentRepository, times(1)).saveAll(any());
        verify(saleRepository, times(2)).save(any(Sale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando armazém não encontrado")
    void create_warehouseNotFound() {
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());

        SaleRequest invalidRequest = new SaleRequest(
                99L, 1L,
                List.of(new SaleItemRequest(1L, 5)),
                List.of(new PaymentRequest(
                        new BigDecimal("129.50"),
                        PaymentMethod.PIX,
                        null
                ))
        );

        assertThrows(WarehouseNotFoundException.class, () -> {
            service.create(invalidRequest);
        });

        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void create_userNotFound() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        SaleRequest invalidRequest = new SaleRequest(
                1L, 99L,
                List.of(new SaleItemRequest(1L, 5)),
                List.of(new PaymentRequest(
                        new BigDecimal("129.50"),
                        PaymentMethod.PIX,
                        null
                ))
        );

        assertThrows(UserNotFoundException.class, () -> {
            service.create(invalidRequest);
        });

        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado")
    void create_productNotFound() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        SaleRequest invalidRequest = new SaleRequest(
                1L, 1L,
                List.of(new SaleItemRequest(99L, 5)),
                List.of(new PaymentRequest(
                        new BigDecimal("129.50"),
                        PaymentMethod.PIX,
                        null
                ))
        );

        assertThrows(ProductNotFoundException.class, () -> {
            service.create(invalidRequest);
        });

        verify(saleItemRepository, never()).saveAll(any());
        verify(paymentRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Deve retornar venda por ID com sucesso")
    void getById_success() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        SaleResponse response = service.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());

        verify(saleRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando venda não encontrada")
    void getById_notFound() {
        when(saleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SaleNotFoundException.class, () -> {
            service.getById(99L);
        });

        verify(saleRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve retornar vendas por armazém com sucesso")
    void getByWarehouseId_success() {
        Page<Sale> page = new PageImpl<>(List.of(sale));
        when(warehouseRepository.existsById(1L)).thenReturn(true);
        when(saleRepository.findByWarehouseId(1L, pageable)).thenReturn(page);

        Page<SaleResponse> response = service.getByWarehouseId(1L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());

        verify(warehouseRepository, times(1)).existsById(1L);
        verify(saleRepository, times(1)).findByWarehouseId(1L, pageable);
    }

    @Test
    @DisplayName("Deve lançar exceção quando armazém não encontrado ao buscar vendas")
    void getByWarehouseId_notFound() {
        when(warehouseRepository.existsById(99L)).thenReturn(false);

        assertThrows(WarehouseNotFoundException.class, () -> {
            service.getByWarehouseId(99L, pageable);
        });

        verify(saleRepository, never()).findByWarehouseId(any(), any());
    }

    @Test
    @DisplayName("Deve retornar vendas por usuário com sucesso")
    void getByUserId_success() {
        Page<Sale> page = new PageImpl<>(List.of(sale));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(saleRepository.findByCreatedById(1L, pageable)).thenReturn(page);

        Page<SaleResponse> response = service.getByUserId(1L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());

        verify(userRepository, times(1)).existsById(1L);
        verify(saleRepository, times(1)).findByCreatedById(1L, pageable);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado ao buscar vendas")
    void getByUserId_notFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> {
            service.getByUserId(99L, pageable);
        });

        verify(saleRepository, never()).findByCreatedById(any(), any());
    }
}