package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.response.PaymentResponse;
import com.luizdev.inventory_system_remastered.entity.Payment;
import com.luizdev.inventory_system_remastered.entity.Sale;
import com.luizdev.inventory_system_remastered.enums.PaymentMethod;
import com.luizdev.inventory_system_remastered.enums.PaymentStatus;
import com.luizdev.inventory_system_remastered.exceptions.paymentExceptions.PaymentAlreadyPaidException;
import com.luizdev.inventory_system_remastered.exceptions.paymentExceptions.PaymentNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.saleExceptions.SaleNotFoundException;
import com.luizdev.inventory_system_remastered.repositories.PaymentRepository;
import com.luizdev.inventory_system_remastered.repositories.SaleRepository;
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
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private PaymentService service;

    private Payment payment;
    private Sale sale;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        sale = new Sale();
        sale.setId(1L);

        payment = new Payment();
        payment.setId(1L);
        payment.setSale(sale);
        payment.setAmount(new BigDecimal("129.50"));
        payment.setPaymentMethod(PaymentMethod.PIX);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaidAt(null);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve confirmar pagamento com sucesso")
    void confirm_success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentResponse response = service.confirm(1L);

        assertNotNull(response);
        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertNotNull(payment.getPaidAt());

        verify(paymentRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento não encontrado ao confirmar")
    void confirm_notFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> {
            service.confirm(99L);
        });

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento já está confirmado")
    void confirm_alreadyPaid() {
        payment.setStatus(PaymentStatus.PAID);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThrows(PaymentAlreadyPaidException.class, () -> {
            service.confirm(1L);
        });

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Deve retornar pagamento por ID com sucesso")
    void getById_success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentResponse response = service.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(PaymentMethod.PIX, response.paymentMethod());
        assertEquals(PaymentStatus.PENDING, response.status());

        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento não encontrado")
    void getById_notFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> {
            service.getById(99L);
        });

        verify(paymentRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve retornar pagamentos por venda com sucesso")
    void getBySaleId_success() {
        when(saleRepository.existsById(1L)).thenReturn(true);
        when(paymentRepository.findBySaleId(1L)).thenReturn(List.of(payment));

        List<PaymentResponse> response = service.getBySaleId(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(PaymentMethod.PIX, response.get(0).paymentMethod());

        verify(saleRepository, times(1)).existsById(1L);
        verify(paymentRepository, times(1)).findBySaleId(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando venda não encontrada ao buscar pagamentos")
    void getBySaleId_saleNotFound() {
        when(saleRepository.existsById(99L)).thenReturn(false);

        assertThrows(SaleNotFoundException.class, () -> {
            service.getBySaleId(99L);
        });

        verify(paymentRepository, never()).findBySaleId(any());
    }

    @Test
    @DisplayName("Deve retornar pagamentos por método com sucesso")
    void getByMethod_success() {
        Page<Payment> page = new PageImpl<>(List.of(payment));
        when(paymentRepository.findByPaymentMethod(PaymentMethod.PIX, pageable))
                .thenReturn(page);

        Page<PaymentResponse> response = service.getByMethod(PaymentMethod.PIX, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(PaymentMethod.PIX, response.getContent().get(0).paymentMethod());

        verify(paymentRepository, times(1)).findByPaymentMethod(PaymentMethod.PIX, pageable);
    }
}