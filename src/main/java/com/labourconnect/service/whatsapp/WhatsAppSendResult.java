package com.labourconnect.service.whatsapp;

/**
 * Result of a send attempt. Failures are returned as data, not thrown, so
 * callers (MessageRouterService, BookingService, etc.) can make business
 * decisions - retry later, mark a conversation stalled - without mandatory
 * try/catch at every call site.
 */
public record WhatsAppSendResult(boolean success, String messageId, String errorMessage) {

    public static WhatsAppSendResult success(String messageId) {
        return new WhatsAppSendResult(true, messageId, null);
    }

    public static WhatsAppSendResult failure(String errorMessage) {
        return new WhatsAppSendResult(false, null, errorMessage);
    }
}