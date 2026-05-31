package com.realestate.api.service;

import com.realestate.api.config.AbstractIntegrationTest;
import com.realestate.api.dto.AuthenticationRequest;
import com.realestate.api.dto.AuthenticationResponse;
import com.realestate.api.dto.RegisterRequest;
import com.realestate.api.model.Role;
import com.realestate.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest extends AbstractIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        AuthenticationResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
        assertTrue(userRepository.findByEmail("test@example.com").isPresent());
    }

    @Test
    void shouldRejectDuplicateEmail() {
        RegisterRequest request = RegisterRequest.builder()
                .name("User One")
                .email("duplicate@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        authService.register(request);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }

    @Test
    void shouldLoginUser() {
        RegisterRequest reg = RegisterRequest.builder()
                .name("Login User")
                .email("login@example.com")
                .password("password123")
                .role(Role.USER)
                .build();
        authService.register(reg);

        AuthenticationRequest loginReq = AuthenticationRequest.builder()
                .email("login@example.com")
                .password("password123")
                .build();

        AuthenticationResponse response = authService.login(loginReq);
        assertNotNull(response);
        assertNotNull(response.getToken());
    }
}
