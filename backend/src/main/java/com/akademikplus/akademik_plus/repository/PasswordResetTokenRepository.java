package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.PasswordResetToken;
import com.akademikplus.akademik_plus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    @Transactional
    void deleteByUser(User user);
}
