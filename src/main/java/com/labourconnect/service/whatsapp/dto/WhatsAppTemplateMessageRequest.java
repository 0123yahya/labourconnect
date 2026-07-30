package com.labourconnect.service.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Matches Meta Cloud API's exact JSON shape for a template message. Internal
 * to the whatsapp package - no consumer of WhatsAppMessageSender ever sees this.
 */
public record WhatsAppTemplateMessageRequest(
        @JsonProperty("messaging_product") String messagingProduct,
        String to,
        String type,
        Template template
) {

    public static WhatsAppTemplateMessageRequest of(String toPhoneNumber, String templateName,
                                                    String languageCode, List<String> bodyParameters) {
        List<Component> components = (bodyParameters == null || bodyParameters.isEmpty())
                ? List.of()
                : List.of(new Component("body", bodyParameters.stream()
                                                .map(param -> new Parameter("text", param))
                                                .toList()));

        return new WhatsAppTemplateMessageRequest(
                "whatsapp",
                toPhoneNumber,
                "template",
                new Template(templateName, new Language(languageCode), components)
        );
    }

    public record Template(String name, Language language, List<Component> components) {
    }

    public record Language(String code) {
    }

    public record Component(String type, List<Parameter> parameters) {
    }

    public record Parameter(String type, String text) {
    }
}