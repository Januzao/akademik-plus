package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.entity.RefreshToken;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.exception.ValidationException;
import com.akademikplus.akademik_plus.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    @InjectMocks
    private RefreshTokenService service;

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        return user;
    }

    @Test
    void create_deletesOldTokenAndSavesNew() {
        User user = buildUser();
        RefreshToken saved = new RefreshToken(1L, "new-token", user, LocalDateTime.now().plusDays(7));
        when(repository.save(any())).thenReturn(saved);

        RefreshToken result = service.create(user);

        verify(repository).deleteByUser(user);
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isEqualTo(user);
        assertThat(captor.getValue().getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void verify_returnsToken_whenValid() {
        User user = buildUser();
        RefreshToken token = new RefreshToken(1L, "valid-token", user, LocalDateTime.now().plusDays(5));
        when(repository.findByToken("valid-token")).thenReturn(Optional.of(token));

        RefreshToken result = service.verify("valid-token");

        assertThat(result.getToken()).isEqualTo("valid-token");
    }

    @Test
    void verify_throws_whenTokenNotFound() {
        when(repository.findByToken("bad-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.verify("bad-token"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    void verify_throws_whenTokenExpired() {
        User user = buildUser();
        RefreshToken expired = new RefreshToken(1L, "expired-token", user, LocalDateTime.now().minusDays(1));
        when(repository.findByToken("expired-token")).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> service.verify("expired-token"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("expired");

        verify(repository).delete(expired);
    }

    @Test
    void deleteByToken_deletesWhenFound() {
        User user = buildUser();
        RefreshToken token = new RefreshToken(1L, "some-token", user, LocalDateTime.now().plusDays(7));
        when(repository.findByToken("some-token")).thenReturn(Optional.of(token));

        service.deleteByToken("some-token");

        verify(repository).delete(token);
    }

    @Test
    void deleteByUser_delegatesToRepository() {
        User user = buildUser();

        service.deleteByUser(user);

        verify(repository).deleteByUser(user);
    }
}
