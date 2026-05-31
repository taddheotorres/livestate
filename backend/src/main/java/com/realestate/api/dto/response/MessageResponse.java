package com.realestate.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Long id;
    private UserSummary sender;
    private UserSummary receiver;
    private PropertySummary property;
    private String content;
    @JsonProperty("read")
    private boolean readMessage;
    private LocalDateTime createdAt;
}
