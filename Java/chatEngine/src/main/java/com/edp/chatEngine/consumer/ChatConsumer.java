package com.edp.chatEngine.consumer;

import com.edp.chatEngine.config.RabbitMQConfig;
import com.edp.chatEngine.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ChatConsumer {

	private static final Logger logger = LoggerFactory.getLogger(ChatConsumer.class);

	@RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
	public void consumePrivateMessage(Message message) {
		logger.info("=== PRIVATE MESSAGE RECEIVED ===");
		logger.info("ID: {}", message.getId());
		logger.info("From: {}", message.getSender());
		logger.info("To: {}", message.getReceiver());
		logger.info("Content: {}", message.getContent());
		logger.info("Timestamp: {}", message.getTimestamp());
		logger.info("================================");
	}

	@RabbitListener(queues = RabbitMQConfig.BROADCAST_QUEUE)
	public void consumeBroadcastMessage(Message message) {
		logger.info("=== BROADCAST MESSAGE RECEIVED ===");
		logger.info("ID: {}", message.getId());
		logger.info("From: {}", message.getSender());
		logger.info("Content: {}", message.getContent());
		logger.info("Timestamp: {}", message.getTimestamp());
		logger.info("==================================");
	}

	@RabbitListener(queues = RabbitMQConfig.DLQ_QUEUE)
	public void consumeFailedMessage(Message message) {
		logger.error("Message sent to DLQ after 3 failed attempts!");
		logger.error("ID: {}", message.getId());
		logger.error("Type: {}", message.getType());
		logger.error("From: {}", message.getSender());
		logger.error("Content: {}", message.getContent());
	}
}