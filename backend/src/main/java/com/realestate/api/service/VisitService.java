package com.realestate.api.service;

import com.realestate.api.exception.ResourceNotFoundException;
import com.realestate.api.model.Property;
import com.realestate.api.model.User;
import com.realestate.api.model.Visit;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

        return visitRepository.save(visit);
    }

    public List<Visit> getMyVisits(Long visitorId) {
        return visitRepository.findByVisitorIdOrderByScheduledDateAsc(visitorId);
    }

    public List<Visit> getIncomingVisits(Long agentId) {
        return visitRepository.findByAgentIdOrderByScheduledDateAsc(agentId);
    }

    @Transactional
    public Visit updateVisitStatus(Long visitId, String status) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found: " + visitId));
        visit.setStatus(Visit.VisitStatus.valueOf(status));
        return visitRepository.save(visit);
    }
}
