package com.akademikplus.akademik_plus.auth;

import com.akademikplus.akademik_plus.entity.PasswordResetToken;
import com.akademikplus.akademik_plus.entity.RefreshToken;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.Role;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.exception.ValidationException;
import com.akademikplus.akademik_plus.repository.PasswordResetTokenRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import com.akademikplus.akademik_plus.security.JwtService;
import com.akademikplus.akademik_plus.service.EmailService;
import com.akademikplus.akademik_plus.service.RefreshTokenService;
import com.akademikplus.akademik_plus.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager manager;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public AuthResponseDTO register(AuthRequestDTO requestDTO) {
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new ValidationException("Email is already registered: " + requestDTO.getEmail());
        }
        User user = new User();
        user.setEmail(requestDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));
        user.setRole(Role.STUDENT);
        user.setFirstName("");
        user.setLastName("");
        user.setIsActive(true);
        userRepository.save(user);

        log.info("New user registered: email={}", requestDTO.getEmail());
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);
        return new AuthResponseDTO(accessToken, refreshToken.getToken());
    }

    public AuthResponseDTO login(AuthRequestDTO requestDTO) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(
                requestDTO.getEmail(),
                requestDTO.getPassword()
        ));
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + requestDTO.getEmail()));

        log.info("User logged in: email={}", requestDTO.getEmail());
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);
        return new AuthResponseDTO(accessToken, refreshToken.getToken());
    }

    public AuthResponseDTO refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService.verify(refreshTokenValue);
        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user);
        log.info("Access token refreshed for user: email={}", user.getEmail());
        return new AuthResponseDTO(newAccessToken, refreshTokenValue);
    }

    public void logout(String accessToken) {
        String email = jwtService.extractUsername(accessToken);
        tokenBlacklistService.blacklist(accessToken);
        userRepository.findByEmail(email).ifPresent(user -> {
            refreshTokenService.deleteByUser(user);
            log.info("User logged out: email={}", email);
        });
    }

    public void changePassword(String email, ChangePasswordDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect.");
        }
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: email={}", email);
    }

    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            passwordResetTokenRepository.deleteByUser(user);
            PasswordResetToken resetToken = new PasswordResetToken(
                    null,
                    UUID.randomUUID().toString(),
                    user,
                    LocalDateTime.now().plusHours(1),
                    false
            );
            passwordResetTokenRepository.save(resetToken);
            emailService.sendPasswordResetEmail(email, resetToken.getToken());
            log.info("Password reset requested for email={}", email);
        });
    }

    public void resetPassword(ResetPasswordDTO dto) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new ValidationException("Invalid or expired reset token."));

        if (resetToken.isUsed()) {
            throw new ValidationException("Reset token has already been used.");
        }
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Reset token has expired.");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        log.info("Password reset completed for email={}", user.getEmail());
    }
}
