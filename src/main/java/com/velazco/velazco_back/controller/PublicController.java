package com.velazco.velazco_back.controller;

import com.velazco.velazco_back.dto.category.responses.CategoryListResponseDto;
import com.velazco.velazco_back.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_back.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_back.dto.product.responses.ProductListResponseDto;
import com.velazco.velazco_back.service.CategoryService;
import com.velazco.velazco_back.service.OrderService;
import com.velazco.velazco_back.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "API Pública", description = "Endpoints públicos para la página web (Tienda en línea)")
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    @Operation(summary = "Obtener todos los productos disponibles")
    @GetMapping("/products")
    public ResponseEntity<List<ProductListResponseDto>> getAvailableProducts() {
        return ResponseEntity.ok(productService.getAllAvailableProducts());
    }

    @Operation(summary = "Obtener todas las categorías")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryListResponseDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(summary = "Crear un nuevo pedido desde la web")
    @PostMapping("/orders")
    public ResponseEntity<OrderStartResponseDto> createWebOrder(@Valid @RequestBody OrderStartRequestDto requestDto) {
        // user is null because it's a public web order
        OrderStartResponseDto responseDto = orderService.startOrder(null, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
