package com.realestate.api.service;

import com.realestate.api.exception.InvalidRequestException;
import com.realestate.api.exception.ResourceNotFoundException;
import com.realestate.api.exception.UnauthorizedException;
import com.realestate.api.model.Booking;
import com.realestate.api.model.User;
import com.realestate.api.repository.BookingRepository;
import com.stripe.Stripe;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
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
    private final BookingService bookingService;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    @Value("${app.base-url:http://localhost:4200}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public Map<String, String> createCheckoutSession(User tenant, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        if (!booking.getTenant().getId().equals(tenant.getId())) {
            throw new UnauthorizedException("You can only pay your own bookings");
        }
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new InvalidRequestException("Only pending bookings can be paid");
        }

        long amountInCents = booking.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        log.info("Creating Stripe checkout session for booking {}: amount={} cents", bookingId, amountInCents);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseUrl + "/dashboard?payment=success")
                .setCancelUrl(baseUrl + "/properties/" + booking.getProperty().getId() + "?payment=cancelled")
                .setClientReferenceId(bookingId.toString())
                .putMetadata("bookingId", bookingId.toString())
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

        try {
            Session session = Session.create(params);
            log.info("Stripe checkout session created for booking {}", bookingId);
            return Map.of("url", session.getUrl());
        } catch (StripeException e) {
            throw new IllegalStateException("Failed to create Stripe checkout session", e);
        }
    }

    /** Stripe calls this after checkout; the signature proves the event really comes from Stripe. */
    public void handleWebhook(String payload, String signature) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            throw new InvalidRequestException("Stripe webhook secret is not configured");
        }
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new InvalidRequestException("Invalid Stripe signature");
        }
        if (!"checkout.session.completed".equals(event.getType())) {
            return;
        }
        Session session;
        try {
            session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
        } catch (EventDataObjectDeserializationException e) {
            throw new InvalidRequestException("Unreadable Stripe event");
        }
        String bookingId = session.getMetadata() == null ? null : session.getMetadata().get("bookingId");
        if ("paid".equals(session.getPaymentStatus()) && bookingId != null) {
            bookingService.markPaid(Long.valueOf(bookingId));
        }
    }
}
