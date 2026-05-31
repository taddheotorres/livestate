package com.realestate.api.service;

import com.realestate.api.config.AbstractIntegrationTest;
import com.realestate.api.model.*;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PropertyServiceTest extends AbstractIntegrationTest {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    private User agent;

    @BeforeEach
    void setUp() {
        propertyRepository.deleteAll();
        userRepository.deleteAll();

        agent = User.builder()
                .name("Agent Test")
                .email("agent@test.com")
                .password("encoded-password")
                .role(Role.AGENT)
                .build();
        agent = userRepository.save(agent);
    }

    @Test
    void shouldCreateProperty() {
        Property property = Property.builder()
                .title("Test Property")
                .description("A test property")
                .price(new BigDecimal("1000"))
                .location("Test Location")
                .bedrooms(3)
                .bathrooms(2)
                .areaSqm(150.0)
                .type(PropertyType.HOUSE)
                .status(PropertyStatus.AVAILABLE)
                .agent(agent)
                .images(new ArrayList<>())
                .build();

        Property saved = propertyService.createProperty(property);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Test Property", saved.getTitle());
        assertEquals(agent.getId(), saved.getAgent().getId());
    }

    @Test
    void shouldFindAllProperties() {
        Property p1 = Property.builder()
                .title("Property 1").price(new BigDecimal("500"))
                .location("Loc").type(PropertyType.HOUSE)
                .status(PropertyStatus.AVAILABLE).agent(agent)
                .images(new ArrayList<>()).build();
        Property p2 = Property.builder()
                .title("Property 2").price(new BigDecimal("700"))
                .location("Loc").type(PropertyType.APARTMENT)
                .status(PropertyStatus.AVAILABLE).agent(agent)
                .images(new ArrayList<>()).build();

        propertyRepository.save(p1);
        propertyRepository.save(p2);

        assertEquals(2, propertyService.getAllProperties().size());
    }

    @Test
    void shouldDeleteProperty() {
        Property property = Property.builder()
                .title("To Delete").price(new BigDecimal("300"))
                .location("Loc").type(PropertyType.HOUSE)
                .status(PropertyStatus.AVAILABLE).agent(agent)
                .images(new ArrayList<>()).build();
        property = propertyRepository.save(property);

        propertyService.deleteProperty(property.getId());

        assertFalse(propertyRepository.findById(property.getId()).isPresent());
    }
}
