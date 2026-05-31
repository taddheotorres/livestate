package com.realestate.api.service;

import com.realestate.api.exception.ResourceNotFoundException;
import com.realestate.api.model.Booking;
import com.realestate.api.repository.BookingRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${app.base-url:http://localhost:4200}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public Map<String, String> createCheckoutSession(Long bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

            long amountInCents = booking.getTotalAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValueExact();

            log.info("Creating Stripe checkout session for booking {}: amount={} cents", bookingId, amountInCents);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(baseUrl + "/dashboard?payment=success")
                    .setCancelUrl(baseUrl + "/properties/" + booking.getProperty().getId() + "?payment=cancelled")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("mxn")
                                                    .setUnitAmount(amountInCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Renta de " + booking.getProperty().getTitle())
                                                                    .build())
                                                    .build())
                                    .build())
                    .build();

            Session session = Session.create(params);
            log.info("Stripe checkout session created: {}", session.getUrl());
            return Map.of("url", session.getUrl());

        } catch (Exception e) {
            log.error("Failed to create Stripe checkout session for booking {}", bookingId, e);
            throw new RuntimeException("Failed to create Stripe checkout session: " + e.getMessage(), e);
        }
    }
}
