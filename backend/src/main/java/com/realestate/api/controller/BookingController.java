package com.realestate.api.controller;

import com.realestate.api.dto.mapper.BookingMapper;
import com.realestate.api.dto.request.BookingRequest;
import com.realestate.api.dto.response.BookingResponse;
import com.realestate.api.model.Booking;
import com.realestate.api.model.User;
import com.realestate.api.security.SecurityUtils;
import com.realestate.api.service.BookingService;
import com.realestate.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User tenant = userService.getCurrentUser(userEmail);

        Booking booking = bookingService.createBooking(
                tenant,
                request.getPropertyId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getTotalAmount(),
                Booking.PaymentMethod.valueOf(request.getPaymentMethod()),
                request.getNotes()
        );

        return ResponseEntity.ok(bookingMapper.toResponse(booking));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User tenant = userService.getCurrentUser(userEmail);
        return ResponseEntity.ok(bookingMapper.toResponseList(bookingService.getMyBookings(tenant.getId())));
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<BookingResponse>> getIncomingBookings() {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User agent = userService.getCurrentUser(userEmail);
        return ResponseEntity.ok(bookingMapper.toResponseList(bookingService.getIncomingBookings(agent.getId())));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(bookingMapper.toResponse(bookingService.updateBookingStatus(id, body.get("status"), userEmail)));
    }
}
