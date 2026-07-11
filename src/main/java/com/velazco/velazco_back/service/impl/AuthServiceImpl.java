package com.velazco.velazco_back.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velazco.velazco_back.dto.auth.request.AuthLoginRequestDto;
import com.velazco.velazco_back.dto.auth.request.ClientRegisterRequestDto;
import com.velazco.velazco_back.dto.auth.response.AuthLoginResponse;
import com.velazco.velazco_back.model.RefreshToken;
import com.velazco.velazco_back.model.Role;
import com.velazco.velazco_back.model.User;
import com.velazco.velazco_back.exceptions.GeneralBadRequestException;
import com.velazco.velazco_back.repositories.RoleRepository;
import com.velazco.velazco_back.repositories.UserRepository;
import com.velazco.velazco_back.security.jwt.JwtTokenProvider;
import com.velazco.velazco_back.service.AuthService;
import com.velazco.velazco_back.service.RefreshTokenService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder encoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;

  @Override
  @Transactional
  public AuthLoginResponse login(AuthLoginRequestDto request, HttpServletRequest httpRequest) {
    log.info("🟦 Intentando iniciar sesión con email: {}", request.getEmail());

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + request.getEmail()));

    log.info("✅ Usuario encontrado: {}", user.getEmail());

    // Verificar si el usuario está activo
    if (user.getActive() == null || !user.getActive()) {
      log.warn("🚫 Usuario inactivo: {}", user.getEmail());
      throw new GeneralBadRequestException("Usuario inactivo. Contacte al administrador.");
    }

    // Verificar contraseña
    boolean passwordMatches = encoder.matches(request.getPassword(), user.getPassword());
    if (!passwordMatches) {
      log.warn("⚠️ Contraseña incorrecta para usuario: {}", user.getEmail());
      throw new GeneralBadRequestException("Contraseña incorrecta");
    }

    log.info("🔐 Contraseña verificada correctamente.");

    // Revocar todos los refresh tokens anteriores del usuario
    refreshTokenService.revokeAllUserTokens(user);
    log.info("♻️ Tokens antiguos del usuario revocados.");

    // Generar tokens nuevos
    String accessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getId()));
    String deviceInfo = getDeviceInfo(httpRequest);
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, deviceInfo);

    log.info("✅ Tokens generados correctamente para usuario {}", user.getEmail());

    return AuthLoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken.getToken())
        .user(AuthLoginResponse.UserDto.builder()
            .id(String.valueOf(user.getId()))
            .nombreCompleto(user.getName())
            .email(user.getEmail())
            .telefono(user.getPhone())
            .build())
        .build();
  }

  @Override
  @Transactional
  public AuthLoginResponse registerClient(ClientRegisterRequestDto request, HttpServletRequest httpRequest) {
    log.info("🟦 Intentando registrar cliente con email: {}", request.getEmail());

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new GeneralBadRequestException("El correo ya está registrado.");
    }

    Role clientRole = roleRepository.findById(6L)
        .orElseThrow(() -> new EntityNotFoundException("Rol de Cliente no encontrado en la base de datos"));

    User newUser = new User();
    newUser.setName(request.getNombreCompleto());
    newUser.setEmail(request.getEmail());
    newUser.setPhone(request.getTelefono());
    newUser.setPassword(encoder.encode(request.getPassword()));
    newUser.setActive(true);
    newUser.setRole(clientRole);

    User savedUser = userRepository.save(newUser);
    log.info("✅ Cliente registrado exitosamente: {}", savedUser.getEmail());

    String accessToken = jwtTokenProvider.generateAccessToken(String.valueOf(savedUser.getId()));
    String deviceInfo = getDeviceInfo(httpRequest);
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser, deviceInfo);

    return AuthLoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken.getToken())
        .user(AuthLoginResponse.UserDto.builder()
            .id(String.valueOf(savedUser.getId()))
            .nombreCompleto(savedUser.getName())
            .email(savedUser.getEmail())
            .telefono(savedUser.getPhone())
            .build())
        .build();
  }

  @Override
  @Transactional
  public void logout(String refreshToken) {
    if (refreshToken != null && !refreshToken.isEmpty()) {
      refreshTokenService.revokeRefreshToken(refreshToken);
      log.info("🚪 Refresh token revocado exitosamente.");
    } else {
      log.warn("⚠️ Intento de logout sin refresh token válido.");
    }
  }

  private String getDeviceInfo(HttpServletRequest request) {
    String userAgent = request.getHeader("User-Agent");
    String ipAddress = getClientIpAddress(request);
    return String.format("IP: %s, UA: %s", ipAddress,
        userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 100)) : "Unknown");
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }
}
