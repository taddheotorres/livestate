package com.realestate.api.service;

import com.realestate.api.config.AbstractIntegrationTest;
import com.realestate.api.exception.ResourceNotFoundException;
import com.realestate.api.model.*;
import com.realestate.api.repository.BookingRepository;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest extends AbstractIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    private User tenant;
    private Property property;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        propertyRepository.deleteAll();
        userRepository.deleteAll();

        User agent = User.builder()
                .name("Agent").email("agent@test.com")
                .password("pwd").role(Role.AGENT).build();
        agent = userRepository.save(agent);

        tenant = User.builder()
                .name("Tenant").email("tenant@test.com")
                .password("pwd").role(Role.USER).build();
        tenant = userRepository.save(tenant);

        property = Property.builder()
                .title("Rental Property").price(new BigDecimal("1000"))
                .location("Loc").type(PropertyType.HOUSE)
                .status(PropertyStatus.AVAILABLE).agent(agent)
                .images(new ArrayList<>()).build();
        property = propertyRepository.save(property);
    }

    @Test
    void shouldCreateBooking() {
        Booking booking = bookingService.createBooking(
                tenant, property.getId(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                new BigDecimal("4000"),
                Booking.PaymentMethod.CARD,
                "Test booking"
        );

        assertNotNull(booking);
        assertNotNull(booking.getId());
        assertEquals(property.getId(), booking.getProperty().getId());
        assertEquals(tenant.getId(), booking.getTenant().getId());
    }

    @Test
    void shouldThrowWhenPropertyNotFound() {
        assertThrows(ResourceNotFoundException.class, () ->
                bookingService.createBooking(
                        tenant, 999L,
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5),
                        new BigDecimal("4000"),
                        Booking.PaymentMethod.CARD,
                        null
                )
        );
    }

    @Test
    void shouldGetMyBookings() {
        bookingService.createBooking(
                tenant, property.getId(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                new BigDecimal("4000"),
                Booking.PaymentMethod.CARD,
                null
        );

        assertEquals(1, bookingService.getMyBookings(tenant.getId()).size());
    }
}
