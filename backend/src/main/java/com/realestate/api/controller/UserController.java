package com.realestate.api.controller;

import com.realestate.api.dto.mapper.UserMapper;
import com.realestate.api.dto.response.UserResponse;
import com.realestate.api.dto.response.UserSummary;
import com.realestate.api.model.User;
import com.realestate.api.security.SecurityUtils;
import com.realestate.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserSummary> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userMapper.toSummary(userService.getUserById(id)));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userService.getCurrentUser(email);
        return ResponseEntity.ok(toUserResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@RequestBody User userDetails) {
        String email = SecurityUtils.getCurrentUserEmail();
        User updated = userService.updateCurrentUser(email, userDetails);
        return ResponseEntity.ok(toUserResponse(updated));
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .rating(user.getRating())
                .reviewsCount(user.getReviewsCount())
                .recommended(user.getRecommended())
                .bio(user.getBio())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
