package com.realestate.api.controller;

import com.realestate.api.dto.mapper.PropertyMapper;
import com.realestate.api.dto.request.PropertyRequest;
import com.realestate.api.dto.response.PropertyResponse;
import com.realestate.api.exception.ResourceNotFoundException;
import com.realestate.api.model.User;
import com.realestate.api.service.PropertyService;
import com.realestate.api.security.SecurityUtils;
import com.realestate.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final PropertyMapper propertyMapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        return ResponseEntity.ok(propertyMapper.toResponseList(propertyService.getAllProperties()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PropertyResponse>> getMyProperties() {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User agent = userService.getCurrentUser(userEmail);
        return ResponseEntity.ok(
                propertyMapper.toResponseList(propertyService.getPropertiesByAgentId(agent.getId()))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id)
                .map(propertyMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(@Valid @RequestBody PropertyRequest request) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User host = userService.getCurrentUser(userEmail);
        var property = propertyMapper.toEntity(request);
        property.setAgent(host);
        return ResponseEntity.ok(propertyMapper.toResponse(propertyService.createProperty(property)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> updateProperty(@PathVariable Long id, @Valid @RequestBody PropertyRequest request) {
        String userEmail = SecurityUtils.getCurrentUserEmail();

        var existing = propertyService.getPropertyById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + id));

        if (!existing.getAgent().getEmail().equals(userEmail)) {
            return ResponseEntity.status(403).build();
        }

        var updated = propertyService.updateProperty(id, propertyMapper.toEntity(request));
        return ResponseEntity.ok(propertyMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        String userEmail = SecurityUtils.getCurrentUserEmail();

        var existing = propertyService.getPropertyById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + id));

        if (!existing.getAgent().getEmail().equals(userEmail)) {
            return ResponseEntity.status(403).build();
        }

        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }
}
