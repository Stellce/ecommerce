package com.example.backend.product;

import com.example.backend.common.dto.PageResponse;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.order.item.OrderItemRepository;
import com.example.backend.product.dto.request.CreateProductRequest;
import com.example.backend.product.dto.request.PatchProductRequest;
import com.example.backend.product.dto.response.ProductResponse;
import com.example.backend.product.mapper.ProductMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest productRequest){
        Product product = mapper.toEntity(productRequest);
        productRepository.save(product);
        return mapper.toResponse(product);
    }

    @Transactional
    public ProductResponse patchProduct(PatchProductRequest patchProductRequest, UUID id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Optional.ofNullable(patchProductRequest.name())
                .ifPresent(product::setName);

        Optional.ofNullable(patchProductRequest.description())
                .ifPresent(product::setDescription);

        Optional.ofNullable(patchProductRequest.price())
                .ifPresent(product::setPrice);

        Optional.ofNullable(patchProductRequest.stock())
                .ifPresent(product::setStock);

        productRepository.save(product);
        return mapper.toResponse(product);
    }

    public ProductResponse getProductById(UUID id){
        return productRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public PageResponse<ProductResponse> getAllProducts(Pageable pageable){
        return PageResponse.of(
                productRepository.findAll(pageable)
                        .map(mapper::toResponse)
        );
    }

    public void deleteProduct(UUID id) {
        if(!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if(orderItemRepository.existsByProductId(id)) {
            throw new AppException(ErrorCode.PRODUCT_IN_USE);
        }

        productRepository.deleteById(id);
    }
}
