package com.velazco.velazco_back.controller;

import com.velazco.velazco_back.dto.PaginatedResponseDto;
import com.velazco.velazco_back.dto.order.requests.OrderConfirmSaleRequestDto;
import com.velazco.velazco_back.dto.order.requests.OrderStartRequestDto;
import com.velazco.velazco_back.dto.order.responses.DailySaleResponseDto;
import com.velazco.velazco_back.dto.order.responses.DeliveredOrderResponseDto;
import com.velazco.velazco_back.dto.order.responses.OrderConfirmDispatchResponseDto;
import com.velazco.velazco_back.dto.order.responses.OrderConfirmSaleResponseDto;
import com.velazco.velazco_back.dto.order.responses.OrderListResponseDto;
import com.velazco.velazco_back.dto.order.responses.OrderStartResponseDto;
import com.velazco.velazco_back.dto.order.responses.PaymentMethodSummaryDto;
import com.velazco.velazco_back.dto.order.responses.TopProductDto;
import com.velazco.velazco_back.dto.order.responses.WeeklySaleResponseDto;
import com.velazco.velazco_back.model.Order;
import com.velazco.velazco_back.model.User;
import com.velazco.velazco_back.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Pedidos", description = "Gestión de pedidos de clientes")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PreAuthorize("hasAnyRole('Administrador','Cajero', 'Entregas', 'Vendedor')")
  @GetMapping("/status/{status}")
  public ResponseEntity<PaginatedResponseDto<OrderListResponseDto>> getOrdersByStatus(
      @PathVariable String status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    Order.OrderStatus orderStatus;
    try {
      orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(orderService.getOrdersByStatus(orderStatus, pageable));
  }

  @PreAuthorize("hasAnyRole('Administrador','Entregas')")
  @GetMapping("/delivered")
  public ResponseEntity<PaginatedResponseDto<DeliveredOrderResponseDto>> getDeliveredOrders(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    return ResponseEntity.ok(orderService.getDeliveredOrders(pageable));
  }

  @PreAuthorize("hasAnyRole('Administrador','Vendedor','Cajero')")
  @PostMapping("/start")
  public ResponseEntity<OrderStartResponseDto> startOrder(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody OrderStartRequestDto orderRequest) {

    OrderStartResponseDto responseDto = orderService.startOrder(user, orderRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  @PreAuthorize("hasAnyRole('Administrador', 'Cajero')")
  @PostMapping("/{id}/confirm-sale")
  public ResponseEntity<OrderConfirmSaleResponseDto> confirmSale(
      @PathVariable Long id,
      @AuthenticationPrincipal User cashier,
      @Valid @RequestBody OrderConfirmSaleRequestDto requestDto) {

    OrderConfirmSaleResponseDto responseDto = orderService.confirmSale(id, cashier, requestDto.getPaymentMethod());

    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  @PreAuthorize("hasAnyRole('Administrador','Entregas')")
  @PostMapping("/{id}/confirm-dispatch")
  public ResponseEntity<OrderConfirmDispatchResponseDto> confirmDispatch(
      @PathVariable Long id,
      @AuthenticationPrincipal User dispatchedBy) {

    OrderConfirmDispatchResponseDto responseDto = orderService.confirmDispatch(id, dispatchedBy);

    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

  }

  @PreAuthorize("hasAnyRole('Administrador','Cajero')")
  @PutMapping("/{id}/cancel")
  public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
    orderService.cancelOrder(id);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('Administrador')")
  @GetMapping("/daily-sales/details")
  public ResponseEntity<List<DailySaleResponseDto>> getDailySalesDetailed() {
    return ResponseEntity.ok(orderService.getDailySalesDetailed());
  }

  @PreAuthorize("hasRole('Administrador')")
  @GetMapping("/weekly-sales/details")
  public List<WeeklySaleResponseDto> getWeeklySalesDetailed() {
    return orderService.getWeeklySalesDetailed();
  }

  @PreAuthorize("hasRole('Administrador')")
  @GetMapping("/top-products/month")
  public ResponseEntity<List<TopProductDto>> getTopSellingProductsOfMonth() {
    return ResponseEntity.ok(orderService.getTopSellingProductsOfCurrentMonth());
  }

  @PreAuthorize("hasRole('Administrador')")
  @GetMapping("/payment-methods/summary")
  public ResponseEntity<List<PaymentMethodSummaryDto>> getSalesByPaymentMethod() {
    return ResponseEntity.ok(orderService.getSalesByPaymentMethod());
  }

}
