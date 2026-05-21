package com.realestate.api.controller;

import com.realestate.api.model.Booking;
import com.realestate.api.model.Property;
import com.realestate.api.model.User;
import com.realestate.api.repository.BookingRepository;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User tenant = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long propertyId = Long.valueOf(body.get("propertyId").toString());
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        Booking booking = Booking.builder()
                .property(property)
                .tenant(tenant)
                .startDate(java.time.LocalDate.parse(body.get("startDate").toString()))
                .endDate(java.time.LocalDate.parse(body.get("endDate").toString()))
                .totalAmount(new java.math.BigDecimal(body.get("totalAmount").toString()))
                .paymentMethod(Booking.PaymentMethod.valueOf(body.get("paymentMethod").toString()))
                .notes(body.containsKey("notes") ? body.get("notes").toString() : null)
                .build();

        return ResponseEntity.ok(bookingRepository.save(booking));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User tenant = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(bookingRepository.findByTenantIdOrderByCreatedAtDesc(tenant.getId()));
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<Booking>> getIncomingBookings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User agent = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(bookingRepository.findByPropertyAgentIdOrderByCreatedAtDesc(agent.getId()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        String newStatus = body.get("status");
        booking.setStatus(Booking.BookingStatus.valueOf(newStatus));
        return ResponseEntity.ok(bookingRepository.save(booking));
    }
}
