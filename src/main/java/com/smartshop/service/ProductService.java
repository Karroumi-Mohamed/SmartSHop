package com.smartshop.service;

import com.smartshop.dto.request.ProductCreateRequest;
import com.smartshop.dto.request.ProductUpdateRequest;
import com.smartshop.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse createProduct(ProductCreateRequest request);
    ProductResponse getProductById(Long id);
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
    void deleteProduct(Long id);
    Page<ProductResponse> getAllProducts(Pageable pageable);
}
