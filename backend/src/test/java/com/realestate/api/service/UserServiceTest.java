package com.realestate.api.service;

import com.realestate.api.config.AbstractIntegrationTest;
import com.realestate.api.exception.ResourceNotFoundException;
import com.realestate.api.model.Role;
import com.realestate.api.model.User;
import com.realestate.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = User.builder()
                .name("Test User")
                .email("testuser@test.com")
                .password("pwd")
                .role(Role.USER)
                .build();
        user = userRepository.save(user);
    }

    @Test
    void shouldGetUserById() {
        User found = userService.getUserById(user.getId());
        assertEquals(user.getId(), found.getId());
        assertEquals("Test User", found.getName());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void shouldGetCurrentUserByEmail() {
        User found = userService.getCurrentUser("testuser@test.com");
        assertNotNull(found);
        assertEquals("testuser@test.com", found.getEmail());
    }

    @Test
    void shouldUpdateCurrentUser() {
        User updates = User.builder()
                .name("Updated Name")
                .bio("Updated bio")
                .build();

        User updated = userService.updateCurrentUser("testuser@test.com", updates);
        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated bio", updated.getBio());
    }
}
