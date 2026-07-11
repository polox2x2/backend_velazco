package com.velazco.velazco_back.controller;

import com.velazco.velazco_back.dto.production.request.ProductionCreateRequestDto;
import com.velazco.velazco_back.dto.production.request.ProductionFinalizeRequestDto;
import com.velazco.velazco_back.dto.production.request.ProductionStatusUpdateRequestDto;
import com.velazco.velazco_back.dto.production.request.ProductionUpdateRequestDto;
import com.velazco.velazco_back.dto.production.response.ProductionCreateResponseDto;
import com.velazco.velazco_back.dto.production.response.ProductionDailyResponseDto;
import com.velazco.velazco_back.dto.production.response.ProductionFinalizeResponseDto;
import com.velazco.velazco_back.dto.production.response.ProductionHistoryResponseDto;
import com.velazco.velazco_back.dto.production.response.ProductionPendingResponseDto;
import com.velazco.velazco_back.dto.production.response.ProductionProcessResponseDto;
import com.velazco.velazco_back.dto.production.response.ProductionStatusUpdateResponseDto;
import com.velazco.velazco_back.dto.production.response.ProductionUpdateResponseDto;
import com.velazco.velazco_back.model.User;
import com.velazco.velazco_back.service.ProductionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Producción", description = "Órdenes de producción en cocina/panadería")
@RestController
@RequestMapping("/api/productions")
@RequiredArgsConstructor
public class ProductionController {
  private final ProductionService productionService;

  @PreAuthorize("hasAnyRole('Administrador','Producción')")
  @GetMapping("/pending")
  public ResponseEntity<List<ProductionPendingResponseDto>> getPendingProductions() {
    List<ProductionPendingResponseDto> response = productionService.getPendingProductions();
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasAnyRole('Administrador','Producción')")
  @GetMapping("/in-process")
  public ResponseEntity<List<ProductionProcessResponseDto>> getProductionsInProcess() {
    List<ProductionProcessResponseDto> response = productionService.getProductionsInProcess();
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasAnyRole('Administrador','Vendedor')")
  @PostMapping
  public ResponseEntity<ProductionCreateResponseDto> createProduction(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody ProductionCreateRequestDto request) {
    ProductionCreateResponseDto response = productionService.createProduction(request, user);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PreAuthorize("hasRole('Administrador')")
  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteProduction(@PathVariable Long id) {
    productionService.deleteProductionById(id);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('Administrador')")
  @PutMapping("/{id}")
  public ResponseEntity<ProductionUpdateResponseDto> updateProduction(
      @PathVariable Long id,
      @AuthenticationPrincipal User user,
      @RequestBody @Valid ProductionUpdateRequestDto request) {
    ProductionUpdateResponseDto response = productionService.updateProduction(id, request, user);
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasAnyRole('Administrador','Producción')")
  @GetMapping("/daily")
  public List<ProductionDailyResponseDto> getDailyProductions() {
    return productionService.getDailyProductions();
  }

  @PreAuthorize("hasAnyRole('Administrador','Producción')")
  @GetMapping("/history")
  public ResponseEntity<List<ProductionHistoryResponseDto>> getCompleteAndIncompleteHistory() {
    return ResponseEntity.ok(productionService.getCompletedAndIncompleteOrders());
  }

  @PreAuthorize("hasAnyRole('Administrador','Producción')")
  @PatchMapping("/{id}/status")
  public ResponseEntity<ProductionStatusUpdateResponseDto> updateProductionStatus(
      @PathVariable Long id,
      @RequestBody ProductionStatusUpdateRequestDto dto) {
    ProductionStatusUpdateResponseDto response = productionService.changePendingToInProcess(id, dto);
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasAnyRole('Administrador','Producción')")
  @PatchMapping("/{id}/finalize")
  public ResponseEntity<ProductionFinalizeResponseDto> finalizeProduction(
      @PathVariable Long id,
      @RequestBody @Valid ProductionFinalizeRequestDto request) {
    ProductionFinalizeResponseDto response = productionService.finalizeProduction(id, request);
    return ResponseEntity.ok(response);
  }

}