package com.nickmenshikov.flux.auth.service;

import com.nickmenshikov.flux.auth.repository.UserRepository;
import com.nickmenshikov.flux.core.dto.AuthTokens;
import com.nickmenshikov.flux.core.dto.LoginRequest;
import com.nickmenshikov.flux.core.dto.RegistrationRequest;
import com.nickmenshikov.flux.core.exception.BadRequestException;
import com.nickmenshikov.flux.core.model.RefreshToken;
import com.nickmenshikov.flux.core.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthTokens login(LoginRequest request) {
        String login = extractLogin(request);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        login,
                        request.password()
                )
        );

        User user = userRepository.findUserByUsername(auth.getName())
                .or(() -> userRepository.findUserByEmail(auth.getName()))
                .orElseThrow();

        return buildAuthResponse(user.getUsername(), user.getId());
    }

    public AuthTokens register(RegistrationRequest request) {
        User user = userService.register(request.username(), request.email(), request.password(), request.confirmPassword());

        return buildAuthResponse(user.getUsername(), user.getId());
    }

    public AuthTokens refresh(String refreshToken) {
        RefreshToken newToken = refreshTokenService.rotate(refreshToken);

        User user = userRepository.findById(newToken.getUserId()).orElseThrow();

        return buildAuthResponse(user.getUsername(), user.getId());
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeByToken(refreshToken);
    }

    private AuthTokens buildAuthResponse(String username, Long userId) {
        String accessToken = jwtService.generateToken(username);
        RefreshToken refreshToken = refreshTokenService.create(userId);
        return new AuthTokens(accessToken, refreshToken.getToken());
    }

    private String extractLogin(LoginRequest request) {
        if (request.username() != null && !request.username().isBlank()) {
            return request.username();
        }

        if (request.email() != null && !request.email().isBlank()) {
            return request.email();
        }

        throw new BadRequestException("Username or email must be provided");
    }
}
