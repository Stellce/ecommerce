package com.example.backend.product.mapper;

import com.example.backend.product.Product;
import com.example.backend.product.dto.request.CreateProductRequest;
import com.example.backend.product.dto.response.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }

    public Product toEntity(CreateProductRequest productRequest){
        return new Product(
                productRequest.name(),
                productRequest.description(),
                productRequest.price(),
                productRequest.stock()
        );
    }
}
