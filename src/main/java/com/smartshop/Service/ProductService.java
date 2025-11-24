package com.smartshop.Service;

import java.util.List;

import com.smartshop.DTO.Request.ProductCreateRequest;
import com.smartshop.DTO.Request.ProductUpdateRequest;
import com.smartshop.DTO.Response.ProductResponse;

public interface ProductService {

    ProductResponse create(ProductCreateRequest request);

    ProductResponse findById(Long id);

    List<ProductResponse> findAll();

    List<ProductResponse> search(String name);

    List<ProductResponse> findLowStock(Integer threshold);

    ProductResponse update(Long id, ProductUpdateRequest request);

    void delete(Long id);
}
