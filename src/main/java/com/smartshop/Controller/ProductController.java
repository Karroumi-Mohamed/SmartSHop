package com.smartshop.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import com.smartshop.DTO.Request.ProductCreateRequest;
import com.smartshop.DTO.Request.ProductUpdateRequest;
import com.smartshop.DTO.Response.ApiResponse;
import com.smartshop.DTO.Response.ProductResponse;
import com.smartshop.Service.ProductService;
import com.smartshop.Util.AuthHelper;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final AuthHelper authHelper;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody ProductCreateRequest request,
            HttpSession session) {
        authHelper.requireAdmin(session);
        ProductResponse product = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(product, "Product created succesfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll(HttpSession session) {
        authHelper.getCurrentUser(session);
        List<ProductResponse> products = productService.findAll();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(
            @PathVariable Long id,
            HttpSession session) {
        authHelper.getCurrentUser(session);
        ProductResponse product = productService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> search(
            @RequestParam String name,
            HttpSession session) {
        authHelper.getCurrentUser(session);
        List<ProductResponse> products = productService.search(name);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findLowStock(
            @RequestParam(defaultValue = "10") Integer threshold,
            HttpSession session) {
        authHelper.requireAdmin(session);
        List<ProductResponse> products = productService.findLowStock(threshold);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request,
            HttpSession session) {
        authHelper.requireAdmin(session);
        ProductResponse product = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            HttpSession session) {
        authHelper.requireAdmin(session);
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null, "Product deleted successfully"));
    }
}
