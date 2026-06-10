package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByToken(String token);
}
