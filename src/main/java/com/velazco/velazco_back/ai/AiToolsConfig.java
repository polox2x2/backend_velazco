package com.velazco.velazco_back.ai;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.PageRequest;

import com.velazco.velazco_back.model.Order;
import com.velazco.velazco_back.model.Product;
import com.velazco.velazco_back.repositories.OrderRepository;
import com.velazco.velazco_back.repositories.ProductRepository;

@Configuration
public class AiToolsConfig {

  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;

  public AiToolsConfig(ProductRepository productRepository, OrderRepository orderRepository) {
    this.productRepository = productRepository;
    this.orderRepository = orderRepository;
  }

  public record ConsultarStockRequest() {}
  public record ProductStockInfo(String name, java.math.BigDecimal price, Integer stock, Boolean active) {}
  public record ConsultarStockResponse(List<ProductStockInfo> products) {}

  @Bean
  @Description("Retorna el stock, precio y estado de los productos (ej. Pan Francés, Croissant, etc.).")
  public Function<ConsultarStockRequest, ConsultarStockResponse> consultarStockActual() {
    return request -> {
      List<ProductStockInfo> list = productRepository.findAll().stream()
          .map(p -> new ProductStockInfo(p.getName(), p.getPrice(), p.getStock(), p.getActive()))
          .collect(Collectors.toList());
      return new ConsultarStockResponse(list);
    };
  }

  public record ActualizarStockRequest(String productName, Integer quantityToAddOrSubtract) {}
  public record ActualizarStockResponse(String message, String updatedProduct, Integer newStock) {}

  @Bean
  @Description("Modifica las unidades disponibles de un producto específico en la base de datos (acepta valores positivos para añadir y negativos para mermas o reducciones).")
  public Function<ActualizarStockRequest, ActualizarStockResponse> actualizarStock() {
    return request -> {
      if (request.productName() == null || request.productName().isBlank()) {
        throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
      }
      Optional<Product> optProduct = productRepository.findByNameIgnoreCase(request.productName());
      if (optProduct.isEmpty()) {
        return new ActualizarStockResponse("No se pudo completar la acción debido a un problema en el servicio de inventario. Producto no encontrado: " + request.productName(), request.productName(), null);
      }
      
      Product product = optProduct.get();
      int newStock = product.getStock() + request.quantityToAddOrSubtract();
      if (newStock < 0) {
        return new ActualizarStockResponse("No se pudo completar la acción. No se puede reducir el stock a menos de 0. Stock actual: " + product.getStock(), product.getName(), product.getStock());
      }
      
      product.setStock(newStock);
      productRepository.save(product);
      
      return new ActualizarStockResponse("Stock actualizado correctamente", product.getName(), newStock);
    };
  }

  public record ObtenerHistorialVentasRequest() {}
  public record SaleInfo(Long orderId, String clientName, String date, String paymentMethod, String deliveryDriver, java.math.BigDecimal totalAmount) {}
  public record ObtenerHistorialVentasResponse(List<SaleInfo> sales) {}

  @Bean
  @Description("Retorna la lista detallada de pedidos completados, no incluyendo ID de pedido creale un numero de orden secuencial, cliente, fecha de orden, método de pago, repartidor y el total en Soles (S/.).")
  public Function<ObtenerHistorialVentasRequest, ObtenerHistorialVentasResponse> obtenerHistorialVentas() {
    return request -> {
      // Obtenemos los ultimos 50 pedidos entregados
      List<Order> orders = orderRepository.findByStatus(Order.OrderStatus.ENTREGADO, PageRequest.of(0, 50)).getContent();
      
      List<SaleInfo> saleInfos = orders.stream().map(order -> {
        String paymentMethod = (order.getSale() != null) ? order.getSale().getPaymentMethod() : "N/A";
        java.math.BigDecimal totalAmount = (order.getSale() != null) ? order.getSale().getTotalAmount() : java.math.BigDecimal.ZERO;
        String driver = (order.getDispatch() != null && order.getDispatch().getDispatchedBy() != null) 
            ? order.getDispatch().getDispatchedBy().getName() : "N/A";
            
        return new SaleInfo(
            order.getId(),
            order.getClientName(),
            order.getDate() != null ? order.getDate().toString() : "N/A",
            paymentMethod,
            driver,
            totalAmount
        );
      }).collect(Collectors.toList());
      
      return new ObtenerHistorialVentasResponse(saleInfos);
    };
  }
}