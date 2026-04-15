package com.nickmenshikov.flux.auth.service;

import com.nickmenshikov.flux.core.dto.AuthTokens;
import com.nickmenshikov.flux.core.dto.LoginRequest;
import com.nickmenshikov.flux.core.dto.RegistrationRequest;
import com.nickmenshikov.flux.core.exception.BadRequestException;
import com.nickmenshikov.flux.core.model.FluxUserDetails;
import com.nickmenshikov.flux.core.model.RefreshToken;
import com.nickmenshikov.flux.core.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
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

        FluxUserDetails userDetails = (FluxUserDetails) auth.getPrincipal();

        return buildAuthResponse(userDetails.getUser());
    }

    public AuthTokens register(RegistrationRequest request) {
        User user = userService.register(request.username(), request.email(), request.password(), request.confirmPassword());

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthTokens refresh(String refreshToken) {
        RefreshToken newToken = refreshTokenService.rotate(refreshToken);

        User user = newToken.getUser();

        return buildAuthResponse(user);
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeByToken(refreshToken);
    }

    private AuthTokens buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.create(user);
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
