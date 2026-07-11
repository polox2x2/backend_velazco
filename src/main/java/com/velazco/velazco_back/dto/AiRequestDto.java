package com.velazco.velazco_back.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AiRequestDto {
    @NotBlank(message = "El mensaje no puede estar vacío")
    private String prompt;
}
