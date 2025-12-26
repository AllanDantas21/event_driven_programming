package com.edp.emails.producer;

import com.edp.emails.config.KafkaConfig;
import com.edp.emails.dto.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class EmailProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailProducer.class);

    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    public EmailProducer(KafkaTemplate<String, EmailMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmail(EmailMessage message) {
        try {
            logger.info("📤 Sending email to topic: {}", message);

            ListenableFuture<SendResult<String, EmailMessage>> future = kafkaTemplate.send(
                KafkaConfig.EMAIL_TOPIC,
                message
            );
            
            future.addCallback(new ListenableFutureCallback<SendResult<String, EmailMessage>>() {
                @Override
                public void onSuccess(SendResult<String, EmailMessage> result) {
                    logger.info("✅ Email sent successfully! ID: {} | Topic: {} | Partition: {} | Offset: {}", 
                        message.id(), 
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }

                @Override
                public void onFailure(Throwable ex) {
                    logger.error("❌ Error sending email: {}", ex.getMessage());
                    throw new RuntimeException("Failed to send email", ex);
                }
            });
            
        } catch (Exception error) {
            logger.error("❌ Error sending email: {}", error.getMessage());
            throw new RuntimeException("Failed to send email", error);
        }
    }
}

