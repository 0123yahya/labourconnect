package com.labourconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for POST /api/clients.
 */
@Getter
@Setter
public class ClientRequest {

    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;

    private String name;
}