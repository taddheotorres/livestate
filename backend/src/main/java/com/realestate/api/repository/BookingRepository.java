package com.realestate.api.repository;

import com.realestate.api.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    List<Booking> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);
    List<Booking> findByPropertyAgentIdOrderByCreatedAtDesc(Long agentId);
}
