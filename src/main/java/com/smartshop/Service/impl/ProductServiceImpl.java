package com.smartshop.Service.impl;

import com.smartshop.DTO.Request.ProductCreateRequest;
import com.smartshop.DTO.Request.ProductUpdateRequest;
import com.smartshop.DTO.Response.ProductResponse;
import com.smartshop.Entity.Product;
import com.smartshop.Exception.ResourceNotFoundException;
import com.smartshop.Mapper.ProductMapper;
import com.smartshop.Repository.ProductRepository;
import com.smartshop.Service.ProductService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductCreateRequest request) {
        Product product = productMapper.toEntity(request);
        product.setDeleted(false);

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (product.getDeleted()) {
            throw new ResourceNotFoundException("Product", id);
        }

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productMapper.toResponseList(productRepository.findByDeletedFalse());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> search(String name) {
        return productMapper.toResponseList(
                productRepository.findByNameContainingIgnoreCase(name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findLowStock(Integer threshold) {
        return productMapper.toResponseList(
                productRepository.findByStockLessThanAndDeletedFalse(threshold));
    }

    @Override
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (product.getDeleted()) {
            throw new ResourceNotFoundException("Product", id);
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setDeleted(true);
        productRepository.save(product);
    }
}
