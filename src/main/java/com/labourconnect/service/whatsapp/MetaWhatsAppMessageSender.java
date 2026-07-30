package com.labourconnect.service.whatsapp;

import com.labourconnect.config.WhatsAppProperties;
import com.labourconnect.service.whatsapp.dto.WhatsAppApiResponse;
import com.labourconnect.service.whatsapp.dto.WhatsAppTemplateMessageRequest;
import com.labourconnect.service.whatsapp.dto.WhatsAppTextMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

/**
 * Meta WhatsApp Cloud API implementation of WhatsAppMessageSender. This is the
 * only class in the codebase aware that "WhatsApp" currently means Meta's
 * Cloud API specifically - every caller depends on the WhatsAppMessageSender
 * interface, so swapping providers later (Twilio, Gupshup, 360dialog) means
 * adding a new implementation class, not touching any caller.
 *
 * Contains no conversation logic, routing logic, role resolution, or business
 * rules - its only responsibility is sending a message and reporting whether
 * it succeeded. RestClient.Builder is injected (rather than built internally)
 * so this class can be unit tested with a mocked builder/MockRestServiceServer
 * without any real network call.
 */
@Service
public class MetaWhatsAppMessageSender implements WhatsAppMessageSender {

    private static final Logger log = LoggerFactory.getLogger(MetaWhatsAppMessageSender.class);

    private final WhatsAppProperties properties;
    private final RestClient restClient;

    public MetaWhatsAppMessageSender(WhatsAppProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder
                .baseUrl(properties.getApiBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getAccessToken())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public WhatsAppSendResult sendTextMessage(String toPhoneNumber, String messageBody) {
        if (toPhoneNumber == null || toPhoneNumber.isBlank()) {
            log.warn("sendTextMessage called with a blank phone number - message not sent");
            return WhatsAppSendResult.failure("Recipient phone number is required");
        }
        if (messageBody == null || messageBody.isBlank()) {
            log.warn("sendTextMessage called with a blank message body - message not sent");
            return WhatsAppSendResult.failure("Message body is required");
        }

        return send(WhatsAppTextMessageRequest.of(toPhoneNumber, messageBody), "text");
    }

    @Override
    public WhatsAppSendResult sendTemplateMessage(String toPhoneNumber, String templateName,
                                                  String languageCode, List<String> bodyParameters) {
        if (toPhoneNumber == null || toPhoneNumber.isBlank()) {
            log.warn("sendTemplateMessage called with a blank phone number - message not sent");
            return WhatsAppSendResult.failure("Recipient phone number is required");
        }
        if (templateName == null || templateName.isBlank()) {
            log.warn("sendTemplateMessage called with a blank template name - message not sent");
            return WhatsAppSendResult.failure("Template name is required");
        }
        if (languageCode == null || languageCode.isBlank()) {
            log.warn("sendTemplateMessage called with a blank language code - message not sent");
            return WhatsAppSendResult.failure("Language code is required");
        }

        return send(WhatsAppTemplateMessageRequest.of(toPhoneNumber, templateName, languageCode, bodyParameters),
                "template");
    }

    private WhatsAppSendResult send(Object requestBody, String messageType) {
        String path = "/%s/%s/messages".formatted(properties.getApiVersion(), properties.getPhoneNumberId());

        try {
            WhatsAppApiResponse response = restClient.post()
                    .uri(path)
                    .body(requestBody)
                    .retrieve()
                    .body(WhatsAppApiResponse.class);

            if (response == null) {
                log.error("WhatsApp {} message send returned an empty response body", messageType);
                return WhatsAppSendResult.failure("Empty response from WhatsApp API");
            }
            if (response.hasError()) {
                log.error("WhatsApp {} message send failed: {}", messageType, response.error().message());
                return WhatsAppSendResult.failure(response.error().message());
            }

            String messageId = response.firstMessageId();
            log.info("WhatsApp {} message sent successfully, messageId={}", messageType, messageId);
            return WhatsAppSendResult.success(messageId);

        } catch (RestClientResponseException e) {
            log.error("WhatsApp {} message send failed with HTTP {}: {}",
                    messageType, e.getStatusCode(), e.getResponseBodyAsString());
            return WhatsAppSendResult.failure(
                    "WhatsApp API returned HTTP " + e.getStatusCode().value() + ": " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("WhatsApp {} message send failed due to an unexpected error", messageType, e);
            return WhatsAppSendResult.failure("Unexpected error sending WhatsApp message: " + e.getMessage());
        }
    }
}