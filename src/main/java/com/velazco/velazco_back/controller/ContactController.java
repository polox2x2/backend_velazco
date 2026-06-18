package com.velazco.velazco_back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.velazco.velazco_back.dto.contact.ContactRequestDto;
import com.velazco.velazco_back.service.EmailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/api/public/contacto")
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Map<String, Boolean>> enviarContacto(@Valid @RequestBody ContactRequestDto dto) {
        String nombreCompleto = dto.getNombre() + " " + dto.getApellido();
        emailService.sendContactEmail(nombreCompleto, dto.getEmail(), dto.getAsunto(), dto.getMensaje());
        return ResponseEntity.ok(Map.of("exito", true));
    }
}
