package com.realestate.api.repository;

import com.realestate.api.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByVisitorIdOrderByScheduledDateAsc(Long visitorId);
    List<Visit> findByAgentIdOrderByScheduledDateAsc(Long agentId);
    List<Visit> findByPropertyIdOrderByScheduledDateAsc(Long propertyId);
}
