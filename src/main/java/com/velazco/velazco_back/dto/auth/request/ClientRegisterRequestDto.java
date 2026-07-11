package com.velazco.velazco_back.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRegisterRequestDto {
  
  @NotBlank(message = "El nombre no puede estar vacío")
  private String nombreCompleto;

  @NotBlank(message = "El correo no puede estar vacío")
  @Email(message = "El correo debe ser válido")
  private String email;

  private String telefono;

  @NotBlank(message = "La contraseña no puede estar vacía")
  @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
  private String password;
}
