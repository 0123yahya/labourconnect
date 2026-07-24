package com.labourconnect.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Request body for POST /api/jobs.
 * Client is identified/created by phone number - no separate "create client" step needed.
 */
@Getter
@Setter
public class JobRequest {

    private String clientPhoneNumber;
    private String clientName;      // optional, only used if this is a new client
    private String serviceType;     // must match a Skill enum value, e.g. "PLUMBER"
    private String area;
    private String preferredDate;   // format: yyyy-MM-dd
    private String budget;
}