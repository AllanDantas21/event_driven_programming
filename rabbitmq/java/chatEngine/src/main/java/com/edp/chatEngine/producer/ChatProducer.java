package com.edp.chatEngine.producer;

import com.edp.chatEngine.config.RabbitMQConfig;
import com.edp.chatEngine.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChatProducer {

	private static final Logger logger = LoggerFactory.getLogger(ChatProducer.class);
	private final RabbitTemplate rabbitTemplate;

	public ChatProducer(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void sendPrivateMessage(Message message) {
		try {
			if (message.getId() == null) {
				message.setId(UUID.randomUUID().toString());
			}
			message.setType(Message.MessageType.PRIVATE);

			logger.info("Sending private message: {} -> {}", message.getSender(), message.getReceiver());

			rabbitTemplate.convertAndSend(
				RabbitMQConfig.CHAT_EXCHANGE,
				RabbitMQConfig.CHAT_ROUTING_KEY,
				message
			);

			logger.info("Private message sent successfully! ID: {}", message.getId());

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

			rabbitTemplate.convertAndSend(
				RabbitMQConfig.BROADCAST_EXCHANGE,
				"",
				message
			);

			logger.info("Broadcast message sent successfully! ID: {}", message.getId());

		} catch (Exception error) {
			logger.error("Error sending broadcast message: {}", error.getMessage());
			throw new RuntimeException("Failed to send broadcast message", error);
		}
	}
}