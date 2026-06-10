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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager manager;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private TokenBlacklistService tokenBlacklistService;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private User buildUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPasswordHash("hashed");
        user.setRole(Role.STUDENT);
        user.setIsActive(true);
        return user;
    }

    @Test
    void register_createsUserAndReturnsTokens() {
        AuthRequestDTO dto = new AuthRequestDTO();
        dto.setEmail("new@example.com");
        dto.setPassword("password123");

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(jwtService.generateToken(any())).thenReturn("access-token");
        RefreshToken refreshToken = new RefreshToken(1L, "refresh-token", null, LocalDateTime.now().plusDays(7));
        when(refreshTokenService.create(any())).thenReturn(refreshToken);

        AuthResponseDTO result = authService.register(dto);

        assertThat(result.getToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        verify(userRepository).save(any());
    }

    @Test
    void register_throwsValidation_whenEmailAlreadyRegistered() {
        AuthRequestDTO dto = new AuthRequestDTO();
        dto.setEmail("existing@example.com");
        dto.setPassword("password123");

        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(buildUser(1L, "existing@example.com")));

        assertThatThrownBy(() -> authService.register(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void login_authenticatesAndReturnsTokens() {
        AuthRequestDTO dto = new AuthRequestDTO();
        dto.setEmail("jan@example.com");
        dto.setPassword("secret");

        User user = buildUser(1L, "jan@example.com");
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("access-token");
        RefreshToken refreshToken = new RefreshToken(1L, "refresh-token", user, LocalDateTime.now().plusDays(7));
        when(refreshTokenService.create(user)).thenReturn(refreshToken);

        AuthResponseDTO result = authService.login(dto);

        verify(manager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(result.getToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void refresh_generatesNewAccessToken() {
        User user = buildUser(1L, "jan@example.com");
        RefreshToken refreshToken = new RefreshToken(1L, "valid-refresh", user, LocalDateTime.now().plusDays(7));
        when(refreshTokenService.verify("valid-refresh")).thenReturn(refreshToken);
        when(jwtService.generateToken(user)).thenReturn("new-access-token");

        AuthResponseDTO result = authService.refresh("valid-refresh");

        assertThat(result.getToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("valid-refresh");
    }

    @Test
    void logout_blacklistsTokenAndDeletesRefreshTokens() {
        User user = buildUser(1L, "jan@example.com");
        when(jwtService.extractUsername("access-token")).thenReturn("jan@example.com");
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(user));

        authService.logout("access-token");

        verify(tokenBlacklistService).blacklist("access-token");
        verify(refreshTokenService).deleteByUser(user);
    }

    @Test
    void changePassword_updatesPassword_whenCurrentCorrect() {
        User user = buildUser(1L, "jan@example.com");
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "hashed")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newHashed");

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("oldPassword");
        dto.setNewPassword("newPassword123");

        authService.changePassword("jan@example.com", dto);

        assertThat(user.getPasswordHash()).isEqualTo("newHashed");
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_throwsValidation_whenCurrentPasswordWrong() {
        User user = buildUser(1L, "jan@example.com");
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashed")).thenReturn(false);

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("wrongPassword");
        dto.setNewPassword("newPassword123");

        assertThatThrownBy(() -> authService.changePassword("jan@example.com", dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Current password is incorrect");
    }

    @Test
    void changePassword_throwsNotFound_whenUserMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.changePassword("missing@example.com", new ChangePasswordDTO()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void forgotPassword_createsResetTokenAndSendsEmail() {
        User user = buildUser(1L, "jan@example.com");
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        authService.forgotPassword("jan@example.com");

        verify(passwordResetTokenRepository).deleteByUser(user);
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getToken()).isNotNull();
        assertThat(captor.getValue().getExpiresAt()).isAfter(LocalDateTime.now());
        verify(emailService).sendPasswordResetEmail(eq("jan@example.com"), anyString());
    }

    @Test
    void forgotPassword_doesNothing_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        authService.forgotPassword("unknown@example.com");

        verify(passwordResetTokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    void resetPassword_updatesPassword_whenTokenValid() {
        User user = buildUser(1L, "jan@example.com");
        PasswordResetToken resetToken = new PasswordResetToken(
                1L, "reset-token-123", user,
                LocalDateTime.now().plusHours(1), false
        );
        when(passwordResetTokenRepository.findByToken("reset-token-123")).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newHashed");

        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken("reset-token-123");
        dto.setNewPassword("newPassword123");

        authService.resetPassword(dto);

        assertThat(user.getPasswordHash()).isEqualTo("newHashed");
        assertThat(resetToken.isUsed()).isTrue();
        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).save(resetToken);
    }

    @Test
    void resetPassword_throwsValidation_whenTokenNotFound() {
        when(passwordResetTokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());

        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken("bad-token");
        dto.setNewPassword("newPassword123");

        assertThatThrownBy(() -> authService.resetPassword(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid or expired reset token");
    }

    @Test
    void resetPassword_throwsValidation_whenTokenAlreadyUsed() {
        User user = buildUser(1L, "jan@example.com");
        PasswordResetToken usedToken = new PasswordResetToken(
                1L, "used-token", user,
                LocalDateTime.now().plusHours(1), true
        );
        when(passwordResetTokenRepository.findByToken("used-token")).thenReturn(Optional.of(usedToken));

        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken("used-token");
        dto.setNewPassword("newPassword123");

        assertThatThrownBy(() -> authService.resetPassword(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("already been used");
    }

    @Test
    void resetPassword_throwsValidation_whenTokenExpired() {
        User user = buildUser(1L, "jan@example.com");
        PasswordResetToken expiredToken = new PasswordResetToken(
                1L, "expired-token", user,
                LocalDateTime.now().minusHours(1), false
        );
        when(passwordResetTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken("expired-token");
        dto.setNewPassword("newPassword123");

        assertThatThrownBy(() -> authService.resetPassword(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("expired");
    }
}
