package com.realestate.api.controller;

import com.realestate.api.model.Property;
import com.realestate.api.model.User;
import com.realestate.api.model.Visit;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.UserRepository;
import com.realestate.api.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class VisitController {

    private final VisitRepository visitRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> scheduleVisit(@RequestBody Map<String, Object> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User visitor = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long propertyId = Long.valueOf(body.get("propertyId").toString());
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        // El agente es el agente de la propiedad
        User agent = property.getAgent();

        Visit visit = Visit.builder()
                .property(property)
                .visitor(visitor)
                .agent(agent)
                .scheduledDate(java.time.LocalDate.parse(body.get("scheduledDate").toString()))
                .scheduledTime(body.containsKey("scheduledTime") && body.get("scheduledTime") != null
                        ? LocalTime.parse(body.get("scheduledTime").toString())
                        : null)
                .notes(body.containsKey("notes") ? body.get("notes").toString() : null)
                .build();

        return ResponseEntity.ok(visitRepository.save(visit));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Visit>> getMyVisits() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(visitRepository.findByVisitorIdOrderByScheduledDateAsc(user.getId()));
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<Visit>> getIncomingVisits() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User agent = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(visitRepository.findByAgentIdOrderByScheduledDateAsc(agent.getId()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Visit> updateVisitStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visita no encontrada"));
        
        String newStatus = body.get("status");
        visit.setStatus(Visit.VisitStatus.valueOf(newStatus));
        return ResponseEntity.ok(visitRepository.save(visit));
    }
}
