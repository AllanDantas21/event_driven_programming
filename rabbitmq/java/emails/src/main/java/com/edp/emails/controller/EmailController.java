package com.edp.emails.controller;

import com.edp.emails.dto.EmailMessage;
import com.edp.emails.enums.EmailType;
import com.edp.emails.producer.EmailProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
public class EmailController {
    
    private final EmailProducer emailProducer;
    
    public EmailController(EmailProducer emailProducer) {
        this.emailProducer = emailProducer;
    }
    
    @PostMapping("/confirmation")
    public ResponseEntity<String> sendConfirmation(@RequestBody EmailRequest request) {
        EmailMessage message = new EmailMessage(
            EmailType.CONFIRMATION,
            request.getRecipient(),
            "Confirm your registration",
            "Click on the link to confirm: http://example.com/confirm"
        );
        
        emailProducer.sendEmail(message);
        return ResponseEntity.ok("Confirmation email sent to queue!");
    }
    
    @PostMapping("/welcome")
    public ResponseEntity<String> sendWelcome(@RequestBody EmailRequest request) {
        EmailMessage message = new EmailMessage(
            EmailType.WELCOME,
            request.getRecipient(),
            "Welcome!",
            "Hello! Welcome to our platform!"
        );
        
        emailProducer.sendEmail(message);
        return ResponseEntity.ok("Welcome email sent to queue!");
    }
    
    @PostMapping("/password-recovery")
    public ResponseEntity<String> sendPasswordRecovery(@RequestBody EmailRequest request) {
        EmailMessage message = new EmailMessage(
            EmailType.PASSWORD_RECOVERY,
            request.getRecipient(),
            "Password recovery",
            "Use this code to recover your password: 123456"
        );
        
        emailProducer.sendEmail(message);
        return ResponseEntity.ok("Password recovery email sent to queue!");
    }
    
    public static class EmailRequest {
        private String recipient;
        
        public String getRecipient() {
            return recipient;
        }
        
        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }
    }
}