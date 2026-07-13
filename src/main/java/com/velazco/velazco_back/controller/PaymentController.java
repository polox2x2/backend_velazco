package com.velazco.velazco_back.controller;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.velazco.velazco_back.dto.payment.PreferenceResponseDto;
import com.velazco.velazco_back.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.javamail.JavaMailSender;

@RestController
@RequestMapping("/api/public/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final JavaMailSender javaMailSender;

    @PostMapping("/{orderId}/create-preference")
    public ResponseEntity<PreferenceResponseDto> createPreference(@PathVariable Long orderId) {
        try {
            PreferenceResponseDto response = paymentService.createPreference(orderId);
            return ResponseEntity.ok(response);
        } catch (MPApiException e) {
            return ResponseEntity.internalServerError().build();
        } catch (MPException e) {
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{orderId}/validate-payment/{paymentId}")
    public ResponseEntity<String> validatePayment(@PathVariable Long orderId, @PathVariable Long paymentId) {
        try {
            paymentService.validatePayment(paymentId, orderId);
            return ResponseEntity.ok("Payment validated");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error validating payment: " + e.getMessage());
        }
    }

    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail() {
        try {
            com.velazco.velazco_back.model.Order testOrder = new com.velazco.velazco_back.model.Order();
            testOrder.setId(9999L);
            testOrder.setClientName("Prueba Sistema");
            testOrder.setClientEmail("deybbithagm@gmail.com");
            testOrder.setDate(java.time.LocalDateTime.now());
            testOrder.setDetails(new java.util.ArrayList<>());
            
            // Llama al servicio (como es Async, puede que no atrape el error sincrónicamente, pero lo intentará)
            // Para probar sincrónicamente, usaremos el mailSender directamente aquí solo para depuración
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(javaMailSender.createMimeMessage(), "utf-8");
            helper.setText("Este es un correo de prueba desde Railway.", true);
            helper.setTo("deybbithagm@gmail.com");
            helper.setSubject("Prueba de Correo Railway");
            helper.setFrom("deybbithagm@gmail.com");
            
            javaMailSender.send(helper.getMimeMessage());
            return ResponseEntity.ok("CORREO ENVIADO CON ÉXITO. Revisa tu bandeja.");
        } catch (Exception e) {
            return ResponseEntity.ok("ERROR AL ENVIAR CORREO: " + e.getMessage() + " | Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"));
        }
    }
}
