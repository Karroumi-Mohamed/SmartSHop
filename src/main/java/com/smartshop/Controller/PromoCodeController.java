package com.smartshop.Controller;

import com.smartshop.DTO.Response.ApiResponse;
import com.smartshop.Entity.PromoCode;
import com.smartshop.Service.PromoCodeService;
import com.smartshop.Util.AuthHelper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;
    private final AuthHelper authHelper;

    @PostMapping
    public ResponseEntity<ApiResponse<PromoCode>> create(
            @RequestParam String code,
            HttpSession session) {
        authHelper.requireAdmin(session);
        PromoCode promoCode = promoCodeService.create(code);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Promo code created successfully", promoCode));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromoCode>>> findAllActive(HttpSession session) {
        authHelper.requireAdmin(session);
        List<PromoCode> promoCodes = promoCodeService.findAllActive();
        return ResponseEntity.ok(ApiResponse.success(promoCodes));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<PromoCode>> findByCode(
            @PathVariable String code,
            HttpSession session) {
        authHelper.requireAdmin(session);
        PromoCode promoCode = promoCodeService.findByCode(code);
        return ResponseEntity.ok(ApiResponse.success(promoCode));
    }

    @GetMapping("/{code}/validate")
    public ResponseEntity<ApiResponse<PromoCode>> validate(
            @PathVariable String code,
            HttpSession session) {
        authHelper.requireAdmin(session);
        PromoCode promoCode = promoCodeService.validate(code);
        return ResponseEntity.ok(ApiResponse.success("Promo code is valid", promoCode));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String code,
            HttpSession session) {
        authHelper.requireAdmin(session);
        promoCodeService.delete(code);
        return ResponseEntity.ok(ApiResponse.<Void>success("Promo code deleted successfully", null));
    }
}
