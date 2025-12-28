package com.edp.chatEngine.producer;

import com.edp.chatEngine.config.KafkaConfig;
import com.edp.chatEngine.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.UUID;

@Service
public class ChatProducer {

	private static final Logger logger = LoggerFactory.getLogger(ChatProducer.class);
	private final KafkaTemplate<String, Message> kafkaTemplate;

	public ChatProducer(KafkaTemplate<String, Message> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendPrivateMessage(Message message) {
		try {
			if (message.getId() == null) {
				message.setId(UUID.randomUUID().toString());
			}
			message.setType(Message.MessageType.PRIVATE);

			logger.info("Sending private message: {} -> {}", message.getSender(), message.getReceiver());

			ListenableFuture<SendResult<String, Message>> future = kafkaTemplate.send(
				KafkaConfig.CHAT_PRIVATE_TOPIC,
				message
			);

			future.addCallback(new ListenableFutureCallback<SendResult<String, Message>>() {
				@Override
				public void onSuccess(SendResult<String, Message> result) {
					logger.info("Private message sent successfully! ID: {} | Topic: {} | Partition: {} | Offset: {}", 
						message.getId(), 
						result.getRecordMetadata().topic(),
						result.getRecordMetadata().partition(),
						result.getRecordMetadata().offset());
				}

				@Override
				public void onFailure(Throwable ex) {
					logger.error("Error sending private message: {}", ex.getMessage());
					throw new RuntimeException("Failed to send private message", ex);
				}
			});

		} catch (Exception error) {
			logger.error("Error sending private message: {}", error.getMessage());
			throw new RuntimeException("Failed to send private message", error);
		}
	}

	public void sendBroadcastMessage(Message message) {
		try {
			if (message.getId() == null) {
				message.setId(UUID.randomUUID().toString());
			}
			message.setType(Message.MessageType.BROADCAST);

			logger.info("Sending broadcast message from: {}", message.getSender());

			ListenableFuture<SendResult<String, Message>> future = kafkaTemplate.send(
				KafkaConfig.CHAT_BROADCAST_TOPIC,
				message
			);

			future.addCallback(new ListenableFutureCallback<SendResult<String, Message>>() {
				@Override
				public void onSuccess(SendResult<String, Message> result) {
					logger.info("Broadcast message sent successfully! ID: {} | Topic: {} | Partition: {} | Offset: {}", 
						message.getId(), 
						result.getRecordMetadata().topic(),
						result.getRecordMetadata().partition(),
						result.getRecordMetadata().offset());
				}

				@Override
				public void onFailure(Throwable ex) {
					logger.error("Error sending broadcast message: {}", ex.getMessage());
					throw new RuntimeException("Failed to send broadcast message", ex);
				}
			});

		} catch (Exception error) {
			logger.error("Error sending broadcast message: {}", error.getMessage());
			throw new RuntimeException("Failed to send broadcast message", error);
		}
	}
}

