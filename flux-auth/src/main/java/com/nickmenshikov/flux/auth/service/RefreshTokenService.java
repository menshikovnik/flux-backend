package com.nickmenshikov.flux.auth.service;

import com.nickmenshikov.flux.auth.repository.RefreshTokenRepository;
import com.nickmenshikov.flux.core.exception.UnauthorizedException;
import com.nickmenshikov.flux.core.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Transactional
    public RefreshToken create(Long userId) {
        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        token.setCreatedAt(Instant.now());

        return refreshTokenRepository.save(token);
    }

    @Transactional
    public RefreshToken rotate(String tokenValue) {
        RefreshToken existing = refreshTokenRepository.findByToken(tokenValue).orElseThrow(
                () -> new UnauthorizedException("Refresh token not found")
        );

        if (existing.isExpired()) {
            refreshTokenRepository.delete(existing);
            throw new UnauthorizedException("Refresh token is expired");
        }

        Long userId = existing.getUserId();
        refreshTokenRepository.delete(existing);

        RefreshToken newToken = new RefreshToken();
        newToken.setUserId(userId);
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        newToken.setCreatedAt(Instant.now());

        return refreshTokenRepository.save(newToken);
    }

    public void revokeByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
