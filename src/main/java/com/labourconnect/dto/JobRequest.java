package com.labourconnect.dto;

/**
 * Request body for POST /api/jobs.
 * Client is identified/created by phone number - no separate "create client" step needed.
 */
public class JobRequest {

    private String clientPhoneNumber;
    private String clientName;      // optional, only used if this is a new client
    private String serviceType;     // must match a Skill enum value, e.g. "PLUMBER"
    private String area;
    private String preferredDate;   // format: yyyy-MM-dd
    private String budget;

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(String preferredDate) {
        this.preferredDate = preferredDate;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }
}
