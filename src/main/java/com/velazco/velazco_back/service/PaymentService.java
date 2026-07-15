package com.velazco.velazco_back.service;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import com.velazco.velazco_back.dto.payment.PreferenceResponseDto;
import com.velazco.velazco_back.model.Order;
import com.velazco.velazco_back.model.OrderDetail;
import com.velazco.velazco_back.repositories.OrderRepository;
import com.velazco.velazco_back.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final com.velazco.velazco_back.repositories.SaleRepository saleRepository;
    private final com.velazco.velazco_back.repositories.UserRepository userRepository;
    private final EmailService emailService;

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Transactional
    public PreferenceResponseDto createPreference(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<PreferenceItemRequest> items = new ArrayList<>();

        for (OrderDetail detail : order.getDetails()) {
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(detail.getProduct().getId().toString())
                    .title(detail.getProduct().getName())
                    .quantity(detail.getQuantity())
                    .unitPrice(detail.getUnitPrice())
                    .build();
            items.add(itemRequest);
        }

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(frontendUrl + "/checkout/success")
                .pending(frontendUrl + "/checkout/pending")
                .failure(frontendUrl + "/checkout/failure")
                .build();

        PreferenceRequest.PreferenceRequestBuilder prefBuilder = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .externalReference(order.getId().toString());

        if (!frontendUrl.contains("localhost")) {
            prefBuilder.autoReturn("approved");
        }

        PreferenceRequest preferenceRequest = prefBuilder.build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return PreferenceResponseDto.builder()
                .preferenceId(preference.getId())
                .initPoint(preference.getInitPoint())
                .build();
    }

    @Value("${app.env:development}")
    private String appEnv;

    @Transactional
    public void validatePayment(Long paymentId, Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == Order.OrderStatus.PAGADO) {
            return;
        }

        com.mercadopago.client.payment.PaymentClient paymentClient = new com.mercadopago.client.payment.PaymentClient();
        com.mercadopago.resources.payment.Payment payment = paymentClient.get(paymentId);

        boolean isTest = "development".equalsIgnoreCase(appEnv) || "test".equalsIgnoreCase(appEnv);
        String mpStatus = payment.getStatus();

        if ("approved".equalsIgnoreCase(mpStatus) || (isTest && "in_process".equalsIgnoreCase(mpStatus))) {
            // Set order to PAGADO instead of PRUEBA_APROBADO so it shows in Admin UI
            order.setStatus(Order.OrderStatus.PAGADO);

            // Calculate total amount for the Sale
            java.math.BigDecimal totalAmount = order.getDetails().stream()
                    .map(detail -> detail.getUnitPrice().multiply(new java.math.BigDecimal(detail.getQuantity())))
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            // Create a Sale record
            com.velazco.velazco_back.model.Sale sale = new com.velazco.velazco_back.model.Sale();
            sale.setSaleDate(java.time.LocalDateTime.now());
            sale.setPaymentMethod("Mercado Pago" + (isTest ? " (Prueba)" : ""));
            sale.setTotalAmount(totalAmount);
            sale.setOrder(order);

            // Assign a default cashier since online sales don't have a specific human cashier
            java.util.List<com.velazco.velazco_back.model.User> users = userRepository.findAll();
            if (!users.isEmpty()) {
                sale.setCashier(users.get(0));
            }

            saleRepository.save(sale);
            order.setSale(sale);
            orderRepository.save(order);

            // Enviar boleta de compra
            try {
                emailService.sendPurchaseReceipt(order);
            } catch (Exception e) {
                System.err.println("Error enviando boleta, pero el pago se registró: " + e.getMessage());
            }

        } else if ("pending".equalsIgnoreCase(mpStatus)) {
            order.setStatus(Order.OrderStatus.PENDIENTE);
            orderRepository.save(order);
        }
    }
}
