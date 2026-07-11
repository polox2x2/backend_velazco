package com.velazco.velazco_back.service;

import com.velazco.velazco_back.dto.auth.request.AuthLoginRequestDto;
import com.velazco.velazco_back.dto.auth.response.AuthLoginResponse;
import com.velazco.velazco_back.dto.auth.request.ClientRegisterRequestDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

  AuthLoginResponse login(AuthLoginRequestDto request, HttpServletRequest httpRequest);

  AuthLoginResponse registerClient(ClientRegisterRequestDto request, HttpServletRequest httpRequest);

  void logout(String refreshToken);
}
