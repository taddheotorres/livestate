package com.realestate.api.dto.response;

import com.realestate.api.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Double rating;
    private Integer reviewsCount;
    private Boolean recommended;
    private String bio;
    private String phone;
    private LocalDateTime createdAt;
}
