package com.velazco.velazco_back.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.velazco.velazco_back.model.Order;
import com.velazco.velazco_back.model.OrderDetail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import org.springframework.scheduling.annotation.Async;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Async
    public void sendContactEmail(String name, String email, String subject, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlMsg = "<h3>Nuevo mensaje de contacto de: " + name + " (" + email + ")</h3>"
                    + "<p><strong>Asunto:</strong> " + subject + "</p>"
                    + "<p><strong>Mensaje:</strong></p>"
                    + "<p>" + message.replace("\n", "<br>") + "</p>";

            helper.setText(htmlMsg, true);
            helper.setTo(adminEmail); // Send to the admin
            helper.setSubject("Nuevo Mensaje de Contacto Web: " + subject);
            helper.setFrom(adminEmail);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Error al enviar email de contacto", e);
        }
    }

    @Async
    public void sendPurchaseReceipt(Order order) {
        String recipient = order.getClientEmail();
        
        // Si el cliente no dejó correo, te lo enviamos a ti como administrador
        if (recipient == null || recipient.isEmpty()) {
            recipient = adminEmail;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDate = order.getDate() != null ? order.getDate().format(formatter) : java.time.LocalDateTime.now().format(formatter);

            StringBuilder htmlMsg = new StringBuilder();
            htmlMsg.append("<h2>¡Gracias por tu compra en Panadería Velazco!</h2>");
            htmlMsg.append("<p>Hola <strong>").append(order.getClientName()).append("</strong>,</p>");
            htmlMsg.append("<p>Tu pedido #").append(order.getId()).append(" ha sido pagado exitosamente y está siendo procesado.</p>");
            htmlMsg.append("<p><strong>Fecha de compra:</strong> ").append(formattedDate).append("</p>");
            
            htmlMsg.append("<h3>Resumen de la Orden</h3>");
            htmlMsg.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>");
            htmlMsg.append("<tr><th>Producto</th><th>Cantidad</th><th>Precio Unit.</th><th>Subtotal</th></tr>");

            BigDecimal total = BigDecimal.ZERO;
            for (OrderDetail detail : order.getDetails()) {
                BigDecimal subtotal = detail.getUnitPrice().multiply(new BigDecimal(detail.getQuantity()));
                total = total.add(subtotal);
                htmlMsg.append("<tr>")
                        .append("<td>").append(detail.getProduct().getName()).append("</td>")
                        .append("<td>").append(detail.getQuantity()).append("</td>")
                        .append("<td>S/ ").append(detail.getUnitPrice()).append("</td>")
                        .append("<td>S/ ").append(subtotal).append("</td>")
                        .append("</tr>");
            }
            
            htmlMsg.append("<tr><td colspan='3' align='right'><strong>Total:</strong></td><td><strong>S/ ").append(total).append("</strong></td></tr>");
            htmlMsg.append("</table>");
            
            htmlMsg.append("<p>Si tienes alguna consulta, por favor contáctanos.</p>");

            helper.setText(htmlMsg.toString(), true);
            helper.setTo(recipient);
            helper.setSubject("Boleta de Compra - Pedido #" + order.getId());
            helper.setFrom(adminEmail);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Error al enviar boleta de compra", e);
        }
    }
}
