package com.realestate.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertySummary {
    private Long id;
    private String title;
    private String location;
    private BigDecimal price;
    private String imageUrl;
}
