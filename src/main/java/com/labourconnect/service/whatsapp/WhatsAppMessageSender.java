package com.labourconnect.service.whatsapp;

import java.util.List;

/**
 * The single contract every future consumer (MessageRouterService,
 * BookingService for reminders, etc.) depends on. Contains no conversation
 * logic, routing logic, role resolution, or business rules - only the ability
 * to send a message and report whether it succeeded.
 *
 * Two message types are exposed because Meta's 24-hour session rule requires
 * template messages for any business-initiated message to a user who hasn't
 * messaged in the last 24 hours (e.g. broadcasting a job offer to a worker).
 * Free-form text is only allowed within an active customer-initiated session.
 */
public interface WhatsAppMessageSender {

    WhatsAppSendResult sendTextMessage(String toPhoneNumber, String messageBody);

    WhatsAppSendResult sendTemplateMessage(String toPhoneNumber, String templateName,
                                           String languageCode, List<String> bodyParameters);
}