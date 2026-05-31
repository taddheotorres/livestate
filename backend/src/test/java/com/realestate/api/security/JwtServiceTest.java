package com.realestate.api.security;

import com.realestate.api.config.AbstractIntegrationTest;
import com.realestate.api.model.Role;
import com.realestate.api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest extends AbstractIntegrationTest {

    @Autowired
    private JwtService jwtService;

    private User createTestUser() {
        return User.builder()
                .id(1L)
                .name("JWT Test")
                .email("jwt@test.com")
                .password("pwd")
                .role(Role.USER)
                .build();
    }

    @Test
    void shouldGenerateToken() {
        User user = createTestUser();
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractUsernameFromToken() {
        User user = createTestUser();
        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);
        assertEquals("jwt@test.com", username);
    }

    @Test
    void shouldValidateToken() {
        User user = createTestUser();
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void shouldRejectTokenForDifferentUser() {
        User user1 = createTestUser();
        User user2 = User.builder()
                .id(2L).name("Other").email("other@test.com")
                .password("pwd").role(Role.USER).build();

        String token = jwtService.generateToken(user1);
        assertFalse(jwtService.isTokenValid(token, user2));
    }
}
