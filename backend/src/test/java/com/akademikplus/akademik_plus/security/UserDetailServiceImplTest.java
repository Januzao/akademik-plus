package com.akademikplus.akademik_plus.security;

import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.Role;
import com.akademikplus.akademik_plus.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Test
    void loadUserByUsername_returnsUserDetails_whenFound() {
        User user = new User();
        user.setId(1L);
        user.setEmail("jan@example.com");
        user.setPasswordHash("hashed");
        user.setRole(Role.STUDENT);
        user.setIsActive(true);
        user.setFirstName("Jan");
        user.setLastName("Kowalski");

        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(user));

        UserDetails result = userDetailService.loadUserByUsername("jan@example.com");

        assertThat(result.getUsername()).isEqualTo("jan@example.com");
        assertThat(result.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
    }

    @Test
    void loadUserByUsername_throwsUsernameNotFoundException_whenMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailService.loadUserByUsername("missing@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("missing@example.com");
    }
}
