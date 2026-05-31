package com.realestate.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSummary {
    private Long id;
    private String name;
    private String email;
    private Double rating;
    private Integer reviewsCount;
    private String bio;
    private String phone;
}
