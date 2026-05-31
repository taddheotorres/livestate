package com.realestate.api.controller;

import com.realestate.api.dto.mapper.PropertyMapper;
import com.realestate.api.dto.response.PropertyResponse;
import com.realestate.api.model.User;
import com.realestate.api.security.SecurityUtils;
import com.realestate.api.service.FavoriteService;
import com.realestate.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final PropertyMapper propertyMapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getMyFavorites() {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User user = userService.getCurrentUser(userEmail);
        return ResponseEntity.ok(
                propertyMapper.toResponseList(favoriteService.getMyFavorites(user.getId()))
        );
    }

    @GetMapping("/check/{propertyId}")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(@PathVariable Long propertyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.ok(Map.of("isFavorite", false));
        }

        try {
            String userEmail = SecurityUtils.getCurrentUserEmail();
            User user = userService.getCurrentUser(userEmail);
            boolean isFavorite = favoriteService.checkFavorite(user.getId(), propertyId);
            return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("isFavorite", false));
        }
    }

    @PostMapping("/{propertyId}/toggle")
    public ResponseEntity<Map<String, Boolean>> toggleFavorite(@PathVariable Long propertyId) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User user = userService.getCurrentUser(userEmail);
        boolean isFavorite = favoriteService.toggleFavorite(user, propertyId);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }
}
