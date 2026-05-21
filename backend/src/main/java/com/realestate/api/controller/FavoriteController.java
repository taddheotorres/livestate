package com.realestate.api.controller;

import com.realestate.api.model.Favorite;
import com.realestate.api.model.Property;
import com.realestate.api.model.User;
import com.realestate.api.repository.FavoriteRepository;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @GetMapping
    public ResponseEntity<List<Property>> getMyFavorites() {
        User user = getAuthenticatedUser();
        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        
        // Mapear la lista de Favoritos a la lista de Propiedades, para que el frontend las pinte fácil
        List<Property> favoriteProperties = favorites.stream()
                .map(Favorite::getProperty)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(favoriteProperties);
    }

    @GetMapping("/check/{propertyId}")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(@PathVariable Long propertyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.ok(Map.of("isFavorite", false));
        }
        
        try {
            User user = getAuthenticatedUser();
            boolean isFavorite = favoriteRepository.existsByUserIdAndPropertyId(user.getId(), propertyId);
            return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("isFavorite", false));
        }
    }

    @PostMapping("/{propertyId}/toggle")
    public ResponseEntity<Map<String, Boolean>> toggleFavorite(@PathVariable Long propertyId) {
        User user = getAuthenticatedUser();
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        Optional<Favorite> existing = favoriteRepository.findByUserIdAndPropertyId(user.getId(), propertyId);

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return ResponseEntity.ok(Map.of("isFavorite", false));
        } else {
            Favorite newFavorite = Favorite.builder()
                    .user(user)
                    .property(property)
                    .build();
            favoriteRepository.save(newFavorite);
            return ResponseEntity.ok(Map.of("isFavorite", true));
        }
    }
}
