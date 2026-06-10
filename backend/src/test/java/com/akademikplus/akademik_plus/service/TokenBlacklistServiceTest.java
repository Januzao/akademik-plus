package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.entity.TokenBlacklist;
import com.akademikplus.akademik_plus.repository.TokenBlacklistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private TokenBlacklistRepository repository;

    @InjectMocks
    private TokenBlacklistService service;

    @Test
    void blacklist_savesTokenToRepository() {
        service.blacklist("test-token-value");

        ArgumentCaptor<TokenBlacklist> captor = ArgumentCaptor.forClass(TokenBlacklist.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getToken()).isEqualTo("test-token-value");
        assertThat(captor.getValue().getBlacklistedAt()).isNotNull();
    }

    @Test
    void isBlacklisted_returnsTrue_whenTokenExists() {
        when(repository.existsByToken("blacklisted-token")).thenReturn(true);

        assertThat(service.isBlacklisted("blacklisted-token")).isTrue();
    }

    @Test
    void isBlacklisted_returnsFalse_whenTokenNotExists() {
        when(repository.existsByToken("clean-token")).thenReturn(false);

        assertThat(service.isBlacklisted("clean-token")).isFalse();
    }
}
