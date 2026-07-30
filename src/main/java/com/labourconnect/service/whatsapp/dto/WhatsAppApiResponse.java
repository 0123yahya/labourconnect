package com.labourconnect.service.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Parses Meta Cloud API's response - either a success shape (messages[0].id)
 * or an error shape (error.message). ignoreUnknown = true so a field Meta
 * adds to the API later doesn't break deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WhatsAppApiResponse(List<Message> messages, Error error) {

    public boolean hasError() {
        return error != null;
    }

    public String firstMessageId() {
        return (messages != null && !messages.isEmpty()) ? messages.get(0).id() : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(String id) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Error(String message, String type, Integer code) {
    }
}