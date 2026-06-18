package com.velazco.velazco_back.security.jwt;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.velazco.velazco_back.model.RefreshToken;
import com.velazco.velazco_back.model.User;
import com.velazco.velazco_back.repositories.UserRepository;
import com.velazco.velazco_back.service.RefreshTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.jwt.enabled", havingValue = "true", matchIfMissing = false)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = getAccessTokenFromRequest(request);
        String refreshToken = getRefreshTokenFromRequest(request);

        if (StringUtils.hasText(accessToken) && jwtTokenProvider.validateToken(accessToken)) {
            authenticateUser(accessToken);
        }

        else if (StringUtils.hasText(refreshToken)) {
            try {
                RefreshToken refreshTokenEntity = refreshTokenService.findByToken(refreshToken);
                refreshTokenService.verifyExpiration(refreshTokenEntity);

                User user = refreshTokenEntity.getUser();
                if (user.getActive()) {

                    refreshTokenService.revokeRefreshToken(refreshToken);

                    String newAccessToken = jwtTokenProvider.generateAccessToken(String.valueOf(user.getId()));
                    String deviceInfo = getDeviceInfo(request);
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user, deviceInfo);

                    setTokenCookies(response, newAccessToken, newRefreshToken.getToken());
                    authenticateUser(newAccessToken);
                }
            } catch (Exception e) {
                clearTokenCookies(response);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String accessToken) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(accessToken);
            User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            if (user.getActive()) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ignored) {}
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("velazco_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("velazco_refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        boolean isProd = !"dev".equalsIgnoreCase(activeProfile);

        ResponseCookie accessTokenCookie = ResponseCookie.from("velazco_token", accessToken)
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite(isProd ? "Strict" : "Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("velazco_refresh_token", refreshToken)
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite(isProd ? "Strict" : "Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    private void clearTokenCookies(HttpServletResponse response) {
        boolean isProd = !"dev".equalsIgnoreCase(activeProfile);

        ResponseCookie accessTokenCookie = ResponseCookie.from("velazco_token", "")
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite(isProd ? "Strict" : "Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("velazco_refresh_token", "")
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite(isProd ? "Strict" : "Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    private String getDeviceInfo(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        String ip = getClientIpAddress(request);
        return "IP: " + ip + ", UA: " + (ua != null ? ua.substring(0, Math.min(ua.length(), 100)) : "Unknown");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) return ip.split(",")[0].trim();
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty()) return ip;
        return request.getRemoteAddr();
    }
}
