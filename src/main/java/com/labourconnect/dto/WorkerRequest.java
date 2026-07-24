package com.labourconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for POST /api/workers.
 * skill must match a Skill enum value, e.g. "PLUMBER" - validated and parsed in WorkerService.
 */
@Getter
@Setter
public class WorkerRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;

    @NotBlank(message = "skill is required")
    private String skill;

    @NotBlank(message = "area is required")
    private String area;
}