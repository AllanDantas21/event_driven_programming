package com.edp.chatEngine.consumer;

import com.edp.chatEngine.config.KafkaConfig;
import com.edp.chatEngine.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class ChatConsumer {

	private static final Logger logger = LoggerFactory.getLogger(ChatConsumer.class);

	@KafkaListener(topics = KafkaConfig.CHAT_PRIVATE_TOPIC, groupId = KafkaConfig.CONSUMER_GROUP_ID)
	public void consumePrivateMessage(
			@Payload Message message,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
			@Header(KafkaHeaders.OFFSET) long offset) {
		logger.info("=== PRIVATE MESSAGE RECEIVED ===");
		logger.info("ID: {}", message.getId());
		logger.info("From: {}", message.getSender());
		logger.info("To: {}", message.getReceiver());
		logger.info("Content: {}", message.getContent());
		logger.info("Timestamp: {}", message.getTimestamp());
		logger.info("Topic: {} | Partition: {} | Offset: {}", topic, partition, offset);
		logger.info("================================");
	}

	@KafkaListener(topics = KafkaConfig.CHAT_BROADCAST_TOPIC, groupId = KafkaConfig.CONSUMER_GROUP_ID)
	public void consumeBroadcastMessage(
			@Payload Message message,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
			@Header(KafkaHeaders.OFFSET) long offset) {
		logger.info("=== BROADCAST MESSAGE RECEIVED ===");
		logger.info("ID: {}", message.getId());
		logger.info("From: {}", message.getSender());
		logger.info("Content: {}", message.getContent());
		logger.info("Timestamp: {}", message.getTimestamp());
		logger.info("Topic: {} | Partition: {} | Offset: {}", topic, partition, offset);
		logger.info("==================================");
	}

	@KafkaListener(topics = KafkaConfig.CHAT_DLQ_TOPIC, groupId = KafkaConfig.CONSUMER_GROUP_ID + "-dlq")
	public void consumeFailedMessage(
			@Payload Message message,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
			@Header(KafkaHeaders.OFFSET) long offset) {
		logger.error("Message sent to DLQ after 3 failed attempts!");
		logger.error("Topic: {} | Partition: {} | Offset: {}", topic, partition, offset);
		logger.error("ID: {}", message.getId());
		logger.error("Type: {}", message.getType());
		logger.error("From: {}", message.getSender());
		logger.error("Content: {}", message.getContent());
	}
}

