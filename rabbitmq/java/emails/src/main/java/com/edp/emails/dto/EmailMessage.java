package com.edp.emails.dto;

import com.edp.emails.enums.EmailType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record EmailMessage(
    String id,
    EmailType type,
    String recipient,
    String subject,
    String body,
    LocalDateTime createdAt
) implements Serializable {
    
    public EmailMessage(EmailType type, String recipient, String subject, String body) {
        this(
            UUID.randomUUID().toString(),
            type,
            recipient,
            subject,
            body,
            LocalDateTime.now()
        );
    }
    
    public EmailMessage {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient não pode ser vazio");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type não pode ser nulo");
        }
    }
}