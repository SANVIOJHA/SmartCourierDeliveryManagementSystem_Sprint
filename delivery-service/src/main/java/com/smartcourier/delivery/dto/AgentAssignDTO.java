package com.smartcourier.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AgentAssignDTO {

    @NotBlank(message = "Agent is required")
    private String agent;
}
