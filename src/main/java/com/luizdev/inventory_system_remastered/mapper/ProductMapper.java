package com.luizdev.inventory_system_remastered.mapper;

import com.luizdev.inventory_system_remastered.dto.request.ProductRequest;
import com.luizdev.inventory_system_remastered.dto.response.ProductResponse;
import com.luizdev.inventory_system_remastered.entity.Product;

public class ProductMapper {

    public static Product toProduct (ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setActive(true);
        product.setBrand(request.brand());
        product.setDescription(request.description());
        product.setPrice(request.price());

        return product;
    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                product.getPrice(),
                product.isActive());
    }
}
