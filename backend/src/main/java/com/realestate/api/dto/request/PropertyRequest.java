package com.realestate.api.dto.request;

import com.realestate.api.model.PropertyStatus;
import com.realestate.api.model.PropertyType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotBlank(message = "Location is required")
    private String location;

    private Integer bedrooms;
    private Integer bathrooms;
    private Double areaSqm;

    @NotNull(message = "Type is required")
    private PropertyType type;

    @NotNull(message = "Status is required")
    private PropertyStatus status;

    @Valid
    private List<ImageRequest> images;
}
