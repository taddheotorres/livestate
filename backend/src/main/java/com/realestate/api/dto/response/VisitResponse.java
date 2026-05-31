package com.realestate.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitResponse {
    private Long id;
    private PropertySummary property;
    private UserSummary visitor;
    private UserSummary agent;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
}
