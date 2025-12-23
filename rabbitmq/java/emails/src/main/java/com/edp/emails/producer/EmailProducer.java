package com.edp.emails.producer;

import com.edp.emails.config.RabbitMQConfig;
import com.edp.emails.dto.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public EmailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmail(EmailMessage message) {
        try {
            logger.info("📤 Sending email to queue: {}", message);

            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                message
            );
            
            logger.info("✅ Email sent successfully! ID: {}", message.id());
            
        } catch (Exception error) {
            logger.error("❌ Error sending email: {}", error.getMessage());
            throw new RuntimeException("Failed to send email", error);
        }
    }
}