package com.nickmenshikov.flux.auth.controller;

import com.nickmenshikov.flux.auth.service.AuthService;
import com.nickmenshikov.flux.core.dto.AuthResponse;
import com.nickmenshikov.flux.core.dto.AuthTokens;
import com.nickmenshikov.flux.core.dto.LoginRequest;
import com.nickmenshikov.flux.core.dto.RegistrationRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login", version = "1.0")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        var tokens = authService.login(request);
        setRefreshCookie(response, tokens.refreshToken());
        return ResponseEntity.ok(new AuthResponse(tokens.accessToken()));
    }

    @PostMapping(value = "/register", version = "1.0")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest request, HttpServletResponse response) {
        AuthTokens tokens = authService.register(request);
        setRefreshCookie(response, tokens.refreshToken());
        return ResponseEntity.ok(new AuthResponse(tokens.accessToken()));
    }

    @PostMapping(value = "/refresh", version = "1.0")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(name = "refresh_token") String refreshToken, HttpServletResponse response){
        AuthTokens tokens = authService.refresh(refreshToken);
        setRefreshCookie(response, tokens.refreshToken());
        return ResponseEntity.ok(new AuthResponse(tokens.accessToken()));
    }

    @PostMapping(value = "/logout", version = "1.0")
    public ResponseEntity<Void> logout(@CookieValue(name = "refresh_token") String refreshToken, HttpServletResponse response) {
        authService.logout(refreshToken);
        clearRefreshCookie(response);
        return ResponseEntity.ok().build();
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); //TODO
        cookie.setPath("/api/auth");
        cookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(cookie);
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); //TODO
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
