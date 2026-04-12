package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.response.PaymentResponse;
import com.luizdev.inventory_system_remastered.entity.Payment;
import com.luizdev.inventory_system_remastered.enums.PaymentMethod;
import com.luizdev.inventory_system_remastered.enums.PaymentStatus;
import com.luizdev.inventory_system_remastered.exceptions.paymentExceptions.PaymentAlreadyPaidException;
import com.luizdev.inventory_system_remastered.exceptions.paymentExceptions.PaymentNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.saleExceptions.SaleNotFoundException;
import com.luizdev.inventory_system_remastered.mapper.PaymentMapper;
import com.luizdev.inventory_system_remastered.repositories.PaymentRepository;
import com.luizdev.inventory_system_remastered.repositories.SaleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SaleRepository saleRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          SaleRepository saleRepository) {
        this.paymentRepository = paymentRepository;
        this.saleRepository = saleRepository;
    }

    @Transactional
    public PaymentResponse confirm(Long id) {
        log.info("Confirmando pagamento ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pagamento não encontrado. ID: {}", id);
                    return new PaymentNotFoundException("Pagamento não encontrado: " + id);
                });

        if (payment.getStatus() == PaymentStatus.PAID) {
            log.warn("Pagamento já está confirmado. ID: {}", id);
            throw new PaymentAlreadyPaidException("Pagamento já está confirmado: " + id);
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment = paymentRepository.save(payment);

        log.info("Pagamento confirmado com sucesso. ID: {}, PaidAt: {}",
                payment.getId(), payment.getPaidAt());
        return PaymentMapper.toResponse(payment);
    }

    public PaymentResponse getById(Long id) {
        log.info("Buscando pagamento por ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pagamento não encontrado. ID: {}", id);
                    return new PaymentNotFoundException("Pagamento não encontrado: " + id);
                });

        log.info("Pagamento encontrado. ID: {}, Status: {}", payment.getId(), payment.getStatus());
        return PaymentMapper.toResponse(payment);
    }

    public List<PaymentResponse> getBySaleId(Long saleId) {
        log.info("Buscando pagamentos por venda ID: {}", saleId);

        if (!saleRepository.existsById(saleId)) {
            log.warn("Venda não encontrada. ID: {}", saleId);
            throw new SaleNotFoundException("Venda não encontrada: " + saleId);
        }

        List<PaymentResponse> payments = paymentRepository.findBySaleId(saleId)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();

        log.info("Total de pagamentos encontrados: {}", payments.size());
        return payments;
    }

    public Page<PaymentResponse> getByMethod(PaymentMethod method, Pageable pageable) {
        log.info("Buscando pagamentos por método: {}", method);

        Page<PaymentResponse> payments = paymentRepository.findByPaymentMethod(method, pageable)
                .map(PaymentMapper::toResponse);

        log.info("Total de pagamentos encontrados: {}", payments.getTotalElements());
        return payments;
    }
}