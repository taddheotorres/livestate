package com.realestate.api.service;

import com.realestate.api.config.AbstractIntegrationTest;
import com.realestate.api.model.*;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.UserRepository;
import com.realestate.api.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VisitServiceTest extends AbstractIntegrationTest {

    @Autowired
    private VisitService visitService;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    private User visitor;
    private Property property;

    @BeforeEach
    void setUp() {
        visitRepository.deleteAll();
        propertyRepository.deleteAll();
        userRepository.deleteAll();

        User agent = User.builder()
                .name("Agent").email("agent@test.com")
                .password("pwd").role(Role.AGENT).build();
        agent = userRepository.save(agent);

        visitor = User.builder()
                .name("Visitor").email("visitor@test.com")
                .password("pwd").role(Role.USER).build();
        visitor = userRepository.save(visitor);

        property = Property.builder()
                .title("Visit Property").price(new BigDecimal("800"))
                .location("Loc").type(PropertyType.HOUSE)
                .status(PropertyStatus.AVAILABLE).agent(agent)
                .images(new ArrayList<>()).build();
        property = propertyRepository.save(property);
    }

    @Test
    void shouldScheduleVisit() {
        Visit visit = visitService.scheduleVisit(
                visitor, property.getId(),
                LocalDate.now().plusDays(3),
                LocalTime.of(10, 0),
                "Morning visit"
        );

        assertNotNull(visit);
        assertNotNull(visit.getId());
        assertEquals(visitor.getId(), visit.getVisitor().getId());
        assertEquals(property.getAgent().getId(), visit.getAgent().getId());
    }

    @Test
    void shouldGetMyVisits() {
        visitService.scheduleVisit(
                visitor, property.getId(),
                LocalDate.now().plusDays(3), null, null
        );

        assertEquals(1, visitService.getMyVisits(visitor.getId()).size());
    }
}
