package com.realestate.api.controller;

import com.realestate.api.exception.InvalidRequestException;
import com.realestate.api.model.User;
import com.realestate.api.security.SecurityUtils;
import com.realestate.api.service.PaymentService;
import com.realestate.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, Long> body) {
        Long bookingId = body.get("bookingId");
        if (bookingId == null) {
            throw new InvalidRequestException("bookingId is required");
        }
        User tenant = userService.getCurrentUser(SecurityUtils.getCurrentUserEmail());
        return ResponseEntity.ok(paymentService.createCheckoutSession(tenant, bookingId));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String payload,
                                        @RequestHeader("Stripe-Signature") String signature) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
}
