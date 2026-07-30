package com.labourconnect.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Binds whatsapp.* properties from application.properties. @Validated ensures
 * the application fails fast at startup if a required credential is missing,
 * rather than failing confusingly on the first real send attempt.
 */
@Component
@ConfigurationProperties(prefix = "whatsapp")
@Validated
@Getter
@Setter
public class WhatsAppProperties {

    @NotBlank(message = "whatsapp.api-base-url must be configured")
    private String apiBaseUrl;

    @NotBlank(message = "whatsapp.api-version must be configured")
    private String apiVersion;

    @NotBlank(message = "whatsapp.phone-number-id must be configured")
    private String phoneNumberId;

    @NotBlank(message = "whatsapp.access-token must be configured")
    private String accessToken;
}