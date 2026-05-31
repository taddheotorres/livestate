package com.realestate.api.service;

import com.realestate.api.model.Property;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    public List<Property> getPropertiesByAgentId(Long agentId) {
        return propertyRepository.findByAgentId(agentId);
    }

    public Property createProperty(Property property) {
        if (property.getImages() != null) {
            property.getImages().forEach(image -> image.setProperty(property));
        }
        return propertyRepository.save(property);
    }

    public Property updateProperty(Long id, Property updated) {
        return propertyRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setDescription(updated.getDescription());
                    existing.setPrice(updated.getPrice());
                    existing.setLocation(updated.getLocation());
                    existing.setBedrooms(updated.getBedrooms());
                    existing.setBathrooms(updated.getBathrooms());
                    existing.setAreaSqm(updated.getAreaSqm());
                    existing.setType(updated.getType());
                    existing.setStatus(updated.getStatus());

                    // Reemplazar imágenes si vienen nuevas
                    if (updated.getImages() != null) {
                        existing.getImages().clear();
                        updated.getImages().forEach(img -> {
                            img.setProperty(existing);
                            existing.getImages().add(img);
                        });
                    }

                    return propertyRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + id));
    }

    public void deleteProperty(Long id) {
        log.info("Deleting property {}", id);
        propertyRepository.deleteById(id);
    }
}
