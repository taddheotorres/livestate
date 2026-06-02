package com.realestate.api.service;

import com.realestate.api.exception.ResourceNotFoundException;
import com.realestate.api.exception.UnauthorizedException;
import com.realestate.api.model.Property;
import com.realestate.api.model.User;
import com.realestate.api.model.Visit;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.VisitRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final PropertyRepository propertyRepository;

    @Transactional
    public Visit scheduleVisit(User visitor, Long propertyId, LocalDate scheduledDate,
                                LocalTime scheduledTime, String notes) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));

        User agent = property.getAgent();

        Visit visit = Visit.builder()
                .property(property)
                .visitor(visitor)
                .agent(agent)
                .scheduledDate(scheduledDate)
                .scheduledTime(scheduledTime)
                .notes(notes)
                .build();

        log.info("Visit scheduled: property={}, visitor={}, date={}", propertyId, visitor.getId(), scheduledDate);
        return visitRepository.save(visit);
    }

    public List<Visit> getMyVisits(Long visitorId) {
        return visitRepository.findByVisitorIdOrderByScheduledDateAsc(visitorId);
    }

    public List<Visit> getIncomingVisits(Long agentId) {
        return visitRepository.findByAgentIdOrderByScheduledDateAsc(agentId);
    }

    @Transactional
    public Visit updateVisitStatus(Long visitId, String status, String userEmail) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found: " + visitId));
        String agentEmail = visit.getAgent().getEmail();
        if (!agentEmail.equals(userEmail)) {
            throw new UnauthorizedException("You are not the agent of this property");
        }
        visit.setStatus(Visit.VisitStatus.valueOf(status));
        log.info("Visit {} status updated to {} by {}", visitId, status, userEmail);
        return visitRepository.save(visit);
    }
}
