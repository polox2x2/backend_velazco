package com.velazco.velazco_back.controller;

import com.velazco.velazco_back.dto.AiRequestDto;
import com.velazco.velazco_back.dto.AiResponseDto;
import com.velazco.velazco_back.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    // Se define el endpoint para recibir peticiones POST y procesarlas con el servicio.
    @PostMapping("/generate")
    public ResponseEntity<AiResponseDto> generate(@Valid @RequestBody AiRequestDto request) {
        AiResponseDto response = aiService.generateResponse(request);
        return ResponseEntity.ok(response);
    }
}
