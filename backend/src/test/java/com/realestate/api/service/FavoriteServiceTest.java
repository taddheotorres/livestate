package com.realestate.api.service;

import com.realestate.api.config.AbstractIntegrationTest;
import com.realestate.api.model.*;
import com.realestate.api.repository.FavoriteRepository;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteServiceTest extends AbstractIntegrationTest {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Property property;

    @BeforeEach
    void setUp() {
        favoriteRepository.deleteAll();
        propertyRepository.deleteAll();
        userRepository.deleteAll();

        User agent = User.builder()
                .name("Agent").email("agent@test.com")
                .password("pwd").role(Role.AGENT).build();
        agent = userRepository.save(agent);

        user = User.builder()
                .name("User").email("user@test.com")
                .password("pwd").role(Role.USER).build();
        user = userRepository.save(user);

        property = Property.builder()
                .title("Fav Property").price(new BigDecimal("500"))
                .location("Loc").type(PropertyType.HOUSE)
                .status(PropertyStatus.AVAILABLE).agent(agent)
                .images(new ArrayList<>()).build();
        property = propertyRepository.save(property);
    }

    @Test
    void shouldToggleFavoriteOn() {
        boolean result = favoriteService.toggleFavorite(user, property.getId());
        assertTrue(result);
        assertTrue(favoriteService.checkFavorite(user.getId(), property.getId()));
    }

    @Test
    void shouldToggleFavoriteOff() {
        favoriteService.toggleFavorite(user, property.getId());
        boolean result = favoriteService.toggleFavorite(user, property.getId());
        assertFalse(result);
        assertFalse(favoriteService.checkFavorite(user.getId(), property.getId()));
    }

    @Test
    void shouldGetMyFavorites() {
        favoriteService.toggleFavorite(user, property.getId());
        assertEquals(1, favoriteService.getMyFavorites(user.getId()).size());
    }
}
