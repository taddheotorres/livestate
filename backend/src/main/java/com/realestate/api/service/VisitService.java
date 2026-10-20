package com.realestate.api.service;

import com.realestate.api.exception.InvalidRequestException;
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
    /** The agent can set any status; the visitor can only cancel their own visit. */
    public Visit updateVisitStatus(User actor, Long visitId, String status) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found: " + visitId));
        Visit.VisitStatus newStatus;
        try {
            newStatus = Visit.VisitStatus.valueOf(String.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid visit status: " + status);
        }

        boolean isAgent = visit.getAgent().getId().equals(actor.getId());
        boolean isVisitorCancelling = visit.getVisitor().getId().equals(actor.getId())
                && newStatus == Visit.VisitStatus.CANCELLED;
        if (!isAgent && !isVisitorCancelling) {
            throw new UnauthorizedException("You cannot change this visit");
        }

        visit.setStatus(newStatus);
        log.info("Visit {} status updated to {} by user {}", visitId, newStatus, actor.getId());
        return visitRepository.save(visit);
    }
}
