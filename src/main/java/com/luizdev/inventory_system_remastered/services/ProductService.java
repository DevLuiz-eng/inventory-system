package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.request.ProductRequest;
import com.luizdev.inventory_system_remastered.dto.response.ProductResponse;
import com.luizdev.inventory_system_remastered.entity.Product;
import com.luizdev.inventory_system_remastered.exceptions.productExceptions.ProductAlreadyInactiveException;
import com.luizdev.inventory_system_remastered.exceptions.productExceptions.ProductNotFoundException;
import com.luizdev.inventory_system_remastered.mapper.ProductMapper;
import com.luizdev.inventory_system_remastered.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        log.info("Criando novo produto: {}", request.name());

        Product product = ProductMapper.toProduct(request);
        product = repository.save(product);

        log.info("Produto criado com sucesso. ID: {}, Nome: {}", product.getId(), product.getName());
        return ProductMapper.toResponse(product);
    }

    public ProductResponse getById(Long id) {
        log.info("Buscando produto por ID: {}", id);

        Product product = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado. ID: {}", id);
                    return new ProductNotFoundException("Produto não encontrado: " + id);
                });

        log.info("Produto encontrado. ID: {}, Nome: {}", product.getId(), product.getName());
        return ProductMapper.toResponse(product);
    }

    public Page<ProductResponse> getAll(Pageable pageable) {
        log.info("Buscando todos os produtos. Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductResponse> products = repository.findAll(pageable)
                .map(ProductMapper::toResponse);

        log.info("Total de produtos encontrados: {}", products.getTotalElements());
        return products;
    }

    public Page<ProductResponse> getAllActive(Pageable pageable) {
        log.info("Buscando produtos ativos. Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductResponse> products = repository.findByActiveTrue(pageable)
                .map(ProductMapper::toResponse);

        log.info("Total de produtos ativos encontrados: {}", products.getTotalElements());
        return products;
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        log.info("Atualizando produto ID: {}", id);

        Product product = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado para atualização. ID: {}", id);
                    return new ProductNotFoundException("Produto não encontrado: " + id);
                });

        product.setName(request.name());
        product.setDescription(request.description());
        product.setBrand(request.brand());
        product.setPrice(request.price());

        product = repository.save(product);

        log.info("Produto atualizado com sucesso. ID: {}, Nome: {}", product.getId(), product.getName());
        return ProductMapper.toResponse(product);
    }

    @Transactional
    public void deactivate(Long id) {
        log.info("Desativando produto ID: {}", id);

        Product product = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado para desativação. ID: {}", id);
                    return new ProductNotFoundException("Produto não encontrado: " + id);
                });

        if (!product.isActive()) {
            log.warn("Produto já está inativo. ID: {}", id);
            throw new ProductAlreadyInactiveException("Produto já está inativo: " + id);
        }

        product.setActive(false);
        repository.save(product);

        log.info("Produto desativado com sucesso. ID: {}", id);
    }
}