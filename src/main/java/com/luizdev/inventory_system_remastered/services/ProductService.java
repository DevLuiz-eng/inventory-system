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
        Product product = ProductMapper.toProduct(request);
        log.info("Criando novo produto.");

        product = repository.save(product);
        log.info("Produto criado.");
        return ProductMapper.toResponse(product);
    }

    public ProductResponse getById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado: " + id));

        return ProductMapper.toResponse(product);
    }

    public List<ProductResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    public List<ProductResponse> getAllActive() {
        return repository.findByActiveTrue()
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado: " + id));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setBrand(request.brand());
        product.setPrice(request.price());

        return ProductMapper.toResponse(product);
    }

    @Transactional
    public void deactivate(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado: " + id));

        if (!product.isActive()) {
            throw new ProductAlreadyInactiveException("Produto já está inativo: " + id);
        }

        product.setActive(false);
    }


}
