package com.edp.chatEngine.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {
	private String id;
	private String sender;
	private String receiver;
	private String content;
	private LocalDateTime timestamp;
	private MessageType type;

	public enum MessageType {
		PRIVATE, BROADCAST
	}

	public Message() {
		this.timestamp = LocalDateTime.now();
	}

	public Message(String sender, String receiver, String content, MessageType type) {
		this.id = UUID.randomUUID().toString();
		this.sender = sender;
		this.receiver = receiver;
		this.content = content;
		this.type = type;
		this.timestamp = LocalDateTime.now();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Message{" +
				"id='" + id + '\'' +
				", sender='" + sender + '\'' +
				", receiver='" + receiver + '\'' +
				", content='" + content + '\'' +
				", timestamp=" + timestamp +
				", type=" + type +
				'}';
	}
}