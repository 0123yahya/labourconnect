package com.labourconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for POST /api/jobs.
 * Client is identified/created by phone number - no separate "create client" step needed.
 */
@Getter
@Setter
public class JobRequest {

    @NotBlank(message = "clientPhoneNumber is required")
    private String clientPhoneNumber;

    private String clientName;      // optional, only used if this is a new client

    @NotBlank(message = "serviceType is required")
    private String serviceType;     // must match a Skill enum value, e.g. "PLUMBER"

    @NotBlank(message = "area is required")
    private String area;

    @Pattern(regexp = "^(|\\d{4}-\\d{2}-\\d{2})$", message = "preferredDate must be in yyyy-MM-dd format")
    private String preferredDate;   // format: yyyy-MM-dd, optional

    private String budget;
}