package com.velazco.velazco_back.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthLoginResponse {
  private String accessToken;
  private String refreshToken;
  private UserDto user;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class UserDto {
      private String id;
      private String nombreCompleto;
      private String email;
      private String telefono;
  }
}
