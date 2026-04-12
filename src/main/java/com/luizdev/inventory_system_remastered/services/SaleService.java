package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.request.PaymentRequest;
import com.luizdev.inventory_system_remastered.dto.request.SaleRequest;
import com.luizdev.inventory_system_remastered.dto.response.SaleResponse;
import com.luizdev.inventory_system_remastered.entity.*;
import com.luizdev.inventory_system_remastered.enums.PaymentStatus;
import com.luizdev.inventory_system_remastered.exceptions.productExceptions.ProductNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.saleExceptions.SaleNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions.WarehouseNotFoundException;
import com.luizdev.inventory_system_remastered.mapper.SaleMapper;
import com.luizdev.inventory_system_remastered.repositories.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final SaleItemRepository saleItemRepository;
    private final PaymentRepository paymentRepository;
    private final StockMovementService stockMovementService;

    public SaleService(
            SaleRepository saleRepository,
            ProductRepository productRepository,
            WarehouseRepository warehouseRepository,
            UserRepository userRepository,
            SaleItemRepository saleItemRepository,
            PaymentRepository paymentRepository,
            StockMovementService stockMovementService) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.saleItemRepository = saleItemRepository;
        this.paymentRepository = paymentRepository;
        this.stockMovementService = stockMovementService;
    }

    @Transactional
    public SaleResponse create(SaleRequest request) {
        log.info("Criando nova venda. Armazém ID: {}, Usuário ID: {}",
                request.warehouseId(), request.userId());

        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> {
                    log.warn("Armazém não encontrado. ID: {}", request.warehouseId());
                    return new WarehouseNotFoundException("Armazém não encontrado: " + request.warehouseId());
                });

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado. ID: {}", request.userId());
                    return new UserNotFoundException("Usuário não encontrado: " + request.userId());
                });

        Sale sale = new Sale();
        sale.setWarehouse(warehouse);
        sale.setCreatedBy(user);
        sale.setTotalAmount(BigDecimal.ZERO);
        sale = saleRepository.save(sale);

        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (var itemRequest : request.items()) {

            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> {
                        log.warn("Produto não encontrado. ID: {}", itemRequest.productId());
                        return new ProductNotFoundException("Produto não encontrado: " + itemRequest.productId());
                    });

            stockMovementService.exit(
                    product,
                    warehouse,
                    itemRequest.quantity(),
                    "Venda #" + sale.getId(),
                    user
            );

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.quantity()));

            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setProduct(product);
            saleItem.setQuantity(itemRequest.quantity());
            saleItem.setUnitPrice(unitPrice);
            saleItem.setSubtotal(subtotal);

            saleItems.add(saleItem);
            totalAmount = totalAmount.add(subtotal);

            log.info("Item adicionado. Produto: {}, Quantidade: {}, Subtotal: {}",
                    product.getName(), itemRequest.quantity(), subtotal);
        }

        saleItemRepository.saveAll(saleItems);

        List<Payment> payments = new ArrayList<>();

        for (PaymentRequest paymentRequest : request.payments()) {
            Payment payment = new Payment();
            payment.setSale(sale);
            payment.setAmount(paymentRequest.amount());
            payment.setPaymentMethod(paymentRequest.paymentMethod());
            payment.setDueDate(paymentRequest.dueDate());
            payment.setStatus(PaymentStatus.PENDING);
            payments.add(payment);

            log.info("Pagamento adicionado. Método: {}, Valor: {}",
                    paymentRequest.paymentMethod(), paymentRequest.amount());
        }

        payments = paymentRepository.saveAll(payments);

        sale.setSaleItems(saleItems);
        sale.setTotalAmount(totalAmount);
        sale = saleRepository.save(sale);

        log.info("Venda criada com sucesso. ID: {}, Total: {}", sale.getId(), sale.getTotalAmount());
        return SaleMapper.toResponse(sale, payments);
    }

    public SaleResponse getById(Long id) {
        log.info("Buscando venda por ID: {}", id);

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Venda não encontrada. ID: {}", id);
                    return new SaleNotFoundException("Venda não encontrada: " + id);
                });

        log.info("Venda encontrada. ID: {}, Total: {}", sale.getId(), sale.getTotalAmount());
        return SaleMapper.toResponse(sale, sale.getPayments());
    }

    public Page<SaleResponse> getByWarehouseId(Long warehouseId, Pageable pageable) {
        log.info("Buscando vendas por armazém ID: {}", warehouseId);

        if (!warehouseRepository.existsById(warehouseId)) {
            throw new WarehouseNotFoundException("Armazém não encontrado: " + warehouseId);
        }

        return saleRepository.findByWarehouseId(warehouseId, pageable)
                .map(sale -> SaleMapper.toResponse(sale, sale.getPayments()));
    }

    public Page<SaleResponse> getByUserId(Long userId, Pageable pageable) {
        log.info("Buscando vendas por usuário ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Usuário não encontrado: " + userId);
        }

        return saleRepository.findByCreatedById(userId, pageable)
                .map(sale -> SaleMapper.toResponse(sale, sale.getPayments()));
    }
}