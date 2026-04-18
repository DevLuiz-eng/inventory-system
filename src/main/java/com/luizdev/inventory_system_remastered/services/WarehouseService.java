package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.request.WarehouseRequest;
import com.luizdev.inventory_system_remastered.dto.response.WarehouseResponse;
import com.luizdev.inventory_system_remastered.entity.Warehouse;
import com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions.WarehouseAlreadyInactiveException;
import com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions.WarehouseNotFoundException;
import com.luizdev.inventory_system_remastered.mapper.WarehouseMapper;
import com.luizdev.inventory_system_remastered.repositories.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WarehouseService {

    private final WarehouseRepository repository;

    public WarehouseService(WarehouseRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
        log.info("Criando novo armazém: {}", request.name());

        Warehouse warehouse = WarehouseMapper.toEntity(request);
        warehouse = repository.save(warehouse);

        log.info("Armazém criado com sucesso. ID: {}, Nome: {}", warehouse.getId(), warehouse.getName());
        return WarehouseMapper.toResponse(warehouse);
    }

    public WarehouseResponse getById(Long id) {
        log.info("Buscando armazém por ID: {}", id);

        Warehouse warehouse = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Armazém não encontrado. ID: {}", id);
                    return new WarehouseNotFoundException("Armazém não encontrado: " + id);
                });

        log.info("Armazém encontrado. ID: {}, Nome: {}", warehouse.getId(), warehouse.getName());
        return WarehouseMapper.toResponse(warehouse);
    }

    public Page<WarehouseResponse> getAll(Pageable pageable) {
        log.info("Buscando todos os armazéns. Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<WarehouseResponse> warehouses = repository.findAll(pageable)
                .map(WarehouseMapper::toResponse);

        log.info("Total de armazéns encontrados: {}", warehouses.getTotalElements());
        return warehouses;
    }

    public Page<WarehouseResponse> getAllActive(Pageable pageable) {
        log.info("Buscando armazéns ativos. Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<WarehouseResponse> warehouses = repository.findByActiveTrueOrderByNameAsc(pageable)
                .map(WarehouseMapper::toResponse);

        log.info("Total de armazéns ativos encontrados: {}", warehouses.getTotalElements());
        return warehouses;
    }

    @Transactional
    public WarehouseResponse update(Long id, WarehouseRequest request) {
        log.info("Atualizando armazém ID: {}", id);

        Warehouse warehouse = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Armazém não encontrado para atualização. ID: {}", id);
                    return new WarehouseNotFoundException("Armazém não encontrado: " + id);
                });

        warehouse.setName(request.name());
        warehouse.setLocation(request.location());
        warehouse = repository.save(warehouse);

        log.info("Armazém atualizado com sucesso. ID: {}, Nome: {}", warehouse.getId(), warehouse.getName());
        return WarehouseMapper.toResponse(warehouse);
    }

    @Transactional
    public void deactivate(Long id) {
        log.info("Desativando armazém ID: {}", id);

        Warehouse warehouse = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Armazém não encontrado para desativação. ID: {}", id);
                    return new WarehouseNotFoundException("Armazém não encontrado: " + id);
                });

        if (!warehouse.getActive()) {
            log.warn("Armazém já está inativo. ID: {}", id);
            throw new WarehouseAlreadyInactiveException("Armazém já está inativo: " + id);
        }

        warehouse.setActive(false);
        repository.save(warehouse);

        log.info("Armazém desativado com sucesso. ID: {}", id);
    }
}