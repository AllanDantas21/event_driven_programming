package com.edp.emails.consumer;

import com.edp.emails.config.RabbitMQConfig;
import com.edp.emails.dto.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EmailConsumer.class);
    private final Random random = new Random();

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void processEmail(EmailMessage message) {
        logger.info("Processing email: {}", message);

        try {
            Thread.sleep(random.nextInt(800, 1200));

            // simulating a failure
            if (shouldSimulateFailure()) {
                throw new RuntimeException("Simulated failure sending email");
            }

            switch (message.type()) {
                case CONFIRMATION -> sendConfirmationEmail(message);
                case WELCOME -> sendWelcomeEmail(message);
                case PASSWORD_RECOVERY -> sendPasswordRecoveryEmail(message);
            }

            logger.info("Email sent successfully: {}", message);
        }
        catch (Exception error) {
            logger.error("Error processing email: {}", error.getMessage());
            throw new RuntimeException(error);
        }
    }

    private void sendConfirmationEmail(EmailMessage message) {
        logger.info("Confirmation sending to: {}", message.recipient());
        logger.info("   Subject: {}", message.subject());
        logger.info("   Body: {}", message.body());
    }
    
    private void sendWelcomeEmail(EmailMessage message) {
        logger.info("Welcome sending to: {}", message.recipient());
        logger.info("   Subject: {}", message.subject());
        logger.info("   Body: {}", message.body());
    }
    
    private void sendPasswordRecoveryEmail(EmailMessage message) {
        logger.info("Password recovery sending to: {}", message.recipient());
        logger.info("   Subject: {}", message.subject());
        logger.info("   Body: {}", message.body());
    }
    
    private boolean shouldSimulateFailure() {
        // 30% chance of failure to test retry and DLQ
        return random.nextInt(100) < 30;
    }

    // Consumer for DLQ
    @RabbitListener(queues = RabbitMQConfig.DLQ_QUEUE)
    public void processFailedEmail(EmailMessage message) {
        logger.error("Email went to DLQ after 3 failed attempts!");
        logger.error("   ID: {}", message.id());
        logger.error("   Type: {}", message.type());
        logger.error("   Recipient: {}", message.recipient());
    }
}