package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.RefreshToken;
import com.akademikplus.akademik_plus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Transactional
    void deleteByUser(User user);
}
