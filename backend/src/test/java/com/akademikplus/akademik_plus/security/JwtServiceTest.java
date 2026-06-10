package com.akademikplus.akademik_plus.security;

import com.akademikplus.akademik_plus.config.JwtConfig;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JwtConfig.class, JwtService.class})
@TestPropertySource(properties = {
        "security.jwt.secret-key=dGhpcy1pcy1hLXZlcnktbG9uZy1zZWNyZXQta2V5LWZvci10ZXN0aW5nLXB1cnBvc2VzLW9ubHk=",
        "security.jwt.expiration-time=3600000"
})
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private User buildUser(String email) {
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPasswordHash("hashed");
        user.setRole(Role.STUDENT);
        user.setIsActive(true);
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        return user;
    }

    @Test
    void generateToken_returnsNonNullToken() {
        User user = buildUser("jan@example.com");
        String token = jwtService.generateToken(user);
        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_returnsCorrectEmail() {
        User user = buildUser("jan@example.com");
        String token = jwtService.generateToken(user);
        String email = jwtService.extractUsername(token);
        assertThat(email).isEqualTo("jan@example.com");
    }

    @Test
    void isTokenValid_returnsTrue_forMatchingUser() {
        User user = buildUser("jan@example.com");
        String token = jwtService.generateToken(user);
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenValid_returnsFalse_forDifferentUser() {
        User user = buildUser("jan@example.com");
        User otherUser = buildUser("other@example.com");
        String token = jwtService.generateToken(user);
        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void generateToken_producesUniqueTokensForDifferentUsers() {
        User user1 = buildUser("user1@example.com");
        User user2 = buildUser("user2@example.com");
        String token1 = jwtService.generateToken(user1);
        String token2 = jwtService.generateToken(user2);
        assertThat(token1).isNotEqualTo(token2);
    }
}
