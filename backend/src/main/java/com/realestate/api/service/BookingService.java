package com.realestate.api.service;

import com.realestate.api.exception.ResourceNotFoundException;
import com.realestate.api.exception.UnauthorizedException;
import com.realestate.api.model.Booking;
import com.realestate.api.model.Property;
import com.realestate.api.model.User;
import com.realestate.api.repository.BookingRepository;
import com.realestate.api.repository.PropertyRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;

    @Transactional
    public Booking createBooking(User tenant, Long propertyId, LocalDate startDate,
                                  LocalDate endDate, BigDecimal totalAmount,
                                  Booking.PaymentMethod paymentMethod, String notes) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));

        Booking booking = Booking.builder()
                .property(property)
                .tenant(tenant)
                .startDate(startDate)
                .endDate(endDate)
                .totalAmount(totalAmount)
                .paymentMethod(paymentMethod)
                .notes(notes)
                .build();

        log.info("Booking created: property={}, tenant={}, amount={}", propertyId, tenant.getId(), totalAmount);
        return bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings(Long tenantId) {
        return bookingRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    public List<Booking> getIncomingBookings(Long agentId) {
        return bookingRepository.findByPropertyAgentIdOrderByCreatedAtDesc(agentId);
    }

    @Transactional
    public Booking updateBookingStatus(Long bookingId, String status, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        String agentEmail = booking.getProperty().getAgent().getEmail();
        if (!agentEmail.equals(userEmail)) {
            throw new UnauthorizedException("You are not the agent of this property");
        }
        booking.setStatus(Booking.BookingStatus.valueOf(status));
        log.info("Booking {} status updated to {} by {}", bookingId, status, userEmail);
        return bookingRepository.save(booking);
    }
}
