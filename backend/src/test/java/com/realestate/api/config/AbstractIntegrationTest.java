package com.realestate.api.config;

import com.realestate.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    // One container for the whole run: Spring caches the context, so a per-class container would leave it pointing at a dead DB
    @ServiceConnection
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("real_estate_db_test")
            .withUsername("test")
            .withPassword("test");

    static {
        mysql.start();
    }

    @Autowired private MessageRepository messageRepository;
    @Autowired private VisitRepository visitRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private FavoriteRepository favoriteRepository;
    @Autowired private PropertyRepository propertyRepository;
    @Autowired private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("jwt.secret-key", () -> "VGVzdFNlY3JldEtleUZvckludGVncmF0aW9uVGVzdGluZzI0Qnl0ZXM=");
        registry.add("stripe.api.key", () -> "sk_test_placeholder");
    }

    /** Children first, so foreign keys never block the cleanup between tests. */
    @BeforeEach
    void cleanDatabase() {
        messageRepository.deleteAll();
        visitRepository.deleteAll();
        bookingRepository.deleteAll();
        favoriteRepository.deleteAll();
        propertyRepository.deleteAll();
        userRepository.deleteAll();
    }
}
