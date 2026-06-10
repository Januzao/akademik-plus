package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.entity.TokenBlacklist;
import com.akademikplus.akademik_plus.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final TokenBlacklistRepository repository;

    public void blacklist(String token) {
        TokenBlacklist entry = new TokenBlacklist(null, token, LocalDateTime.now());
        repository.save(entry);
    }

    public boolean isBlacklisted(String token) {
        return repository.existsByToken(token);
    }
}
