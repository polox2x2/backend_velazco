package com.velazco.velazco_back.controller;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.velazco.velazco_back.dto.payment.PreferenceResponseDto;
import com.velazco.velazco_back.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}/create-preference")
    public ResponseEntity<PreferenceResponseDto> createPreference(@PathVariable Long orderId) {
        try {
            PreferenceResponseDto response = paymentService.createPreference(orderId);
            return ResponseEntity.ok(response);
        } catch (MPApiException e) {
            System.err.println("=== MERCADO PAGO API ERROR ===");
            System.err.println("Status Code: " + e.getStatusCode());
            System.err.println("Response Content: " + e.getApiResponse().getContent());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } catch (MPException e) {
            System.err.println("=== MERCADO PAGO SDK ERROR ===");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{orderId}/validate-payment/{paymentId}")
    public ResponseEntity<String> validatePayment(@PathVariable Long orderId, @PathVariable Long paymentId) {
        try {
            paymentService.validatePayment(paymentId, orderId);
            return ResponseEntity.ok("Payment validated");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
