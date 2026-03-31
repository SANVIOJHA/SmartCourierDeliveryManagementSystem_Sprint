package com.smartcourier.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Used to update delivery status
 */
@Data
public class StatusUpdateDTO {
    @NotBlank(message = "Status is required")
    private String status;

    private String location;

    private String description;
}
