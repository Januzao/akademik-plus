package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.entity.RefreshToken;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.exception.ValidationException;
import com.akademikplus.akademik_plus.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private static final int REFRESH_TOKEN_DAYS = 7;

    private final RefreshTokenRepository repository;

    public RefreshToken create(User user) {
        repository.deleteByUser(user);
        RefreshToken token = new RefreshToken(
                null,
                UUID.randomUUID().toString(),
                user,
                LocalDateTime.now().plusDays(REFRESH_TOKEN_DAYS)
        );
        return repository.save(token);
    }

    public RefreshToken verify(String tokenValue) {
        RefreshToken token = repository.findByToken(tokenValue)
                .orElseThrow(() -> new ValidationException("Invalid refresh token."));
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            repository.delete(token);
            throw new ValidationException("Refresh token has expired. Please log in again.");
        }
        return token;
    }

    public void deleteByToken(String tokenValue) {
        repository.findByToken(tokenValue).ifPresent(repository::delete);
    }

    public void deleteByUser(User user) {
        repository.deleteByUser(user);
    }
}
