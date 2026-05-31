package com.realestate.api.dto.response;

import com.realestate.api.model.PropertyStatus;
import com.realestate.api.model.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String location;
    private Integer bedrooms;
    private Integer bathrooms;
    private Double areaSqm;
    private PropertyType type;
    private PropertyStatus status;
    private UserSummary agent;
    private List<ImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
