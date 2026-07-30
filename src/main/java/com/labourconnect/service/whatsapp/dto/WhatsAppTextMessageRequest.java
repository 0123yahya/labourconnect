package com.labourconnect.service.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Matches Meta Cloud API's exact JSON shape for a text message. Internal to
 * the whatsapp package - no consumer of WhatsAppMessageSender ever sees this.
 */
public record WhatsAppTextMessageRequest(
        @JsonProperty("messaging_product") String messagingProduct,
        String to,
        String type,
        TextBody text
) {

    public static WhatsAppTextMessageRequest of(String toPhoneNumber, String messageBody) {
        return new WhatsAppTextMessageRequest("whatsapp", toPhoneNumber, "text", new TextBody(messageBody));
    }

    public record TextBody(String body) {
    }
}