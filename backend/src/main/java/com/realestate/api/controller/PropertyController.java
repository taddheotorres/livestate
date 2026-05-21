package com.realestate.api.controller;

import com.realestate.api.model.Property;
import com.realestate.api.model.User;
import com.realestate.api.service.PropertyService;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PropertyController {

    private final PropertyService propertyService;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/my")
    public ResponseEntity<List<Property>> getMyProperties() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User agent = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(propertyRepository.findByAgentId(agent.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody Property property) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        User host = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        property.setAgent(host);
        
        Property savedProperty = propertyService.createProperty(property);
        return ResponseEntity.ok(savedProperty);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @RequestBody Property property) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Verificar que la propiedad pertenece al usuario autenticado
        Property existing = propertyService.getPropertyById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        if (!existing.getAgent().getEmail().equals(userEmail)) {
            return ResponseEntity.status(403).build();
        }

        Property updated = propertyService.updateProperty(id, property);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Verificar que la propiedad pertenece al usuario autenticado
        Property existing = propertyService.getPropertyById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        if (!existing.getAgent().getEmail().equals(userEmail)) {
            return ResponseEntity.status(403).build();
        }

        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }
}
