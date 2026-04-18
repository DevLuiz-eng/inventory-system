package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.request.StockMovementRequest;
import com.luizdev.inventory_system_remastered.dto.response.StockMovementResponse;
import com.luizdev.inventory_system_remastered.entity.*;
import com.luizdev.inventory_system_remastered.enums.TypeOfStockMovement;
import com.luizdev.inventory_system_remastered.exceptions.inventoryExceptions.InsufficientStockException;
import com.luizdev.inventory_system_remastered.exceptions.productExceptions.ProductNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions.WarehouseNotFoundException;
import com.luizdev.inventory_system_remastered.mapper.StockMovementMapper;
import com.luizdev.inventory_system_remastered.repositories.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;

    public StockMovementService(
            StockMovementRepository stockMovementRepository,
            ProductRepository productRepository,
            WarehouseRepository warehouseRepository,
            UserRepository userRepository,
            InventoryRepository inventoryRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public StockMovementResponse entry(StockMovementRequest request) {
        log.info("Registrando entrada de estoque. Produto ID: {}, Armazém ID: {}, Quantidade: {}",
                request.productId(), request.warehouseId(), request.quantity());

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado. ID: {}", request.productId());
                    return new ProductNotFoundException("Produto não encontrado: " + request.productId());
                });

        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> {
                    log.warn("Armazém não encontrado. ID: {}", request.warehouseId());
                    return new WarehouseNotFoundException("Armazém não encontrado: " + request.warehouseId());
                });

        User user = userRepository.findById(request.createdBy())
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado. ID: {}", request.createdBy());
                    return new UserNotFoundException("Usuário não encontrado: " + request.createdBy());
                });


        Inventory inventory = findOrCreateInventory(product, warehouse);

        inventory.setQuantity(inventory.getQuantity() + request.quantity());
        inventoryRepository.save(inventory);

        log.info("Estoque atualizado após entrada. Produto: {}, Novo total: {}",
                product.getName(), inventory.getQuantity());

        StockMovement movement = StockMovementMapper.toEntity(request, product, warehouse, user);
        movement = stockMovementRepository.save(movement);

        log.info("Entrada de estoque registrada com sucesso. ID: {}", movement.getId());
        return StockMovementMapper.toResponse(movement);
    }


    @Transactional
    public void exit(Product product, Warehouse warehouse,
                     Integer quantity, String reason, User user) {
        log.info("Registrando saída de estoque. Produto: {}, Armazém: {}, Quantidade: {}",
                product.getName(), warehouse.getName(), quantity);

        // Busca o inventário
        Inventory inventory = inventoryRepository
                .findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElseThrow(() -> {
                    log.warn("Inventário não encontrado. Produto: {}, Armazém: {}",
                            product.getName(), warehouse.getName());
                    return new InsufficientStockException(
                            "Produto " + product.getName() + " não possui estoque no armazém: " + warehouse.getName());
                });

        // Valida estoque suficiente
        if (inventory.getQuantity() < quantity) {
            log.warn("Estoque insuficiente. Produto: {}, Disponível: {}, Solicitado: {}",
                    product.getName(), inventory.getQuantity(), quantity);
            throw new InsufficientStockException(
                    "Estoque insuficiente para o produto: " + product.getName() +
                            ". Disponível: " + inventory.getQuantity() +
                            ", Solicitado: " + quantity);
        }

        // Subtrai a quantidade
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);

        log.info("Estoque atualizado após saída. Produto: {}, Novo total: {}",
                product.getName(), inventory.getQuantity());

        // Registra a movimentação
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setCreatedBy(user);
        movement.setType(TypeOfStockMovement.EXIT);
        movement.setQuantity(quantity);
        movement.setReason(reason);
        stockMovementRepository.save(movement);

        log.info("Saída de estoque registrada com sucesso. Produto: {}, Quantidade: {}",
                product.getName(), quantity);
    }

    private Inventory findOrCreateInventory(Product product, Warehouse warehouse) {
        return inventoryRepository
                .findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElseGet(() -> {
                    log.info("Inventário não encontrado, criando novo. Produto: {}, Armazém: {}",
                            product.getName(), warehouse.getName());
                    Inventory newInventory = new Inventory();
                    newInventory.setProduct(product);
                    newInventory.setWarehouse(warehouse);
                    newInventory.setQuantity(0);
                    return inventoryRepository.save(newInventory);
                });
    }

    public Page<StockMovementResponse> getAll(Pageable pageable) {
        log.info("Buscando todas as movimentações. Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<StockMovementResponse> movements = stockMovementRepository.findAll(pageable)
                .map(StockMovementMapper::toResponse);

        log.info("Total de movimentações encontradas: {}", movements.getTotalElements());
        return movements;
    }

    public Page<StockMovementResponse> getByProductId(Long productId, Pageable pageable) {
        log.info("Buscando movimentações por produto ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Produto não encontrado: " + productId);
        }

        return stockMovementRepository.findByProductId(productId, pageable)
                .map(StockMovementMapper::toResponse);
    }

    public Page<StockMovementResponse> getByWarehouseId(Long warehouseId, Pageable pageable) {
        log.info("Buscando movimentações por armazém ID: {}", warehouseId);

        if (!warehouseRepository.existsById(warehouseId)) {
            throw new WarehouseNotFoundException("Armazém não encontrado: " + warehouseId);
        }

        return stockMovementRepository.findByWarehouseId(warehouseId, pageable)
                .map(StockMovementMapper::toResponse);
    }
}