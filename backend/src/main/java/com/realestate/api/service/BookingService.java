package com.realestate.api.service;

import com.realestate.api.exception.InvalidRequestException;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;

    @Transactional
    public Booking createBooking(User tenant, Long propertyId, LocalDate startDate,
                                  LocalDate endDate, Booking.PaymentMethod paymentMethod, String notes) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));

        if (!endDate.isAfter(startDate)) {
            throw new InvalidRequestException("End date must be after start date");
        }
        if (property.getAgent().getId().equals(tenant.getId())) {
            throw new InvalidRequestException("You cannot book your own property");
        }
        if (bookingRepository.existsByPropertyIdAndStatusNotAndStartDateLessThanAndEndDateGreaterThan(
                propertyId, Booking.BookingStatus.CANCELLED, endDate, startDate)) {
            throw new InvalidRequestException("The property is already booked for those dates");
        }
        BigDecimal totalAmount = calculateTotal(property.getPrice(), startDate, endDate);

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

    /** Price is monthly; a stay is charged per day at price / 30, same formula the frontend shows. */
    static BigDecimal calculateTotal(BigDecimal monthlyPrice, LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return monthlyPrice.multiply(BigDecimal.valueOf(days))
                .divide(BigDecimal.valueOf(30), 0, RoundingMode.HALF_UP);
    }

    /** The property's agent can set any status; the tenant can only cancel their own booking. */
    @Transactional
    public Booking updateBookingStatus(User actor, Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        Booking.BookingStatus newStatus = parseStatus(status);

        boolean isAgent = booking.getProperty().getAgent().getId().equals(actor.getId());
        boolean isTenantCancelling = booking.getTenant().getId().equals(actor.getId())
                && newStatus == Booking.BookingStatus.CANCELLED;
        if (!isAgent && !isTenantCancelling) {
            throw new UnauthorizedException("You cannot change this booking");
        }

        booking.setStatus(newStatus);
        log.info("Booking {} status updated to {} by user {}", bookingId, newStatus, actor.getId());
        return bookingRepository.save(booking);
    }

    @Transactional
    public void markPaid(Long bookingId) {
        bookingRepository.findById(bookingId).ifPresentOrElse(booking -> {
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            log.info("Booking {} confirmed after Stripe payment", bookingId);
        }, () -> log.warn("Stripe payment for unknown booking {}", bookingId));
    }

    private static Booking.BookingStatus parseStatus(String status) {
        try {
            return Booking.BookingStatus.valueOf(String.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid booking status: " + status);
        }
    }
}
