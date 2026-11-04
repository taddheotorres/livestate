package com.realestate.api.service;

import com.realestate.api.config.AbstractIntegrationTest;
import com.realestate.api.exception.InvalidRequestException;
import com.realestate.api.exception.UnauthorizedException;
import com.realestate.api.model.*;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest extends AbstractIntegrationTest {

    @Autowired private PaymentService paymentService;
    @Autowired private BookingService bookingService;
    @Autowired private PropertyRepository propertyRepository;
    @Autowired private UserRepository userRepository;

    private User user(String email, Role role) {
        return userRepository.save(User.builder().name(email).email(email).password("pwd").role(role).build());
    }

    @Test
    void shouldRefuseCheckoutForSomeoneElsesBooking() {
        User agent = user("agent@test.com", Role.AGENT);
        User tenant = user("tenant@test.com", Role.USER);
        User stranger = user("stranger@test.com", Role.USER);
        Property property = propertyRepository.save(Property.builder()
                .title("P").price(new BigDecimal("900")).location("L").type(PropertyType.HOUSE)
                .status(PropertyStatus.AVAILABLE).agent(agent).images(new ArrayList<>()).build());
        Booking booking = bookingService.createBooking(tenant, property.getId(),
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), Booking.PaymentMethod.CARD, null);

        assertThrows(UnauthorizedException.class,
                () -> paymentService.createCheckoutSession(stranger, booking.getId()));
    }

    @Test
    void shouldRejectWebhookWhenSecretIsMissing() {
        // the test profile sets no STRIPE_WEBHOOK_SECRET, so no event can be trusted
        assertThrows(InvalidRequestException.class,
                () -> paymentService.handleWebhook("{}", "t=1,v1=forged"));
    }
}
