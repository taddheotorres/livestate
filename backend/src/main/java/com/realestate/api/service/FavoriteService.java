package com.realestate.api.service;

import com.realestate.api.exception.ResourceNotFoundException;
import com.realestate.api.model.Favorite;
import com.realestate.api.model.Property;
import com.realestate.api.model.User;
import com.realestate.api.repository.FavoriteRepository;
import com.realestate.api.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PropertyRepository propertyRepository;

    public List<Property> getMyFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return favorites.stream()
                .map(Favorite::getProperty)
                .toList();
    }

    public boolean checkFavorite(Long userId, Long propertyId) {
        return favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId);
    }

    @Transactional
    public boolean toggleFavorite(User user, Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));

        Optional<Favorite> existing = favoriteRepository.findByUserIdAndPropertyId(user.getId(), propertyId);

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return false;
        } else {
            Favorite newFavorite = Favorite.builder()
                    .user(user)
                    .property(property)
                    .build();
            favoriteRepository.save(newFavorite);
            return true;
        }
    }
}
