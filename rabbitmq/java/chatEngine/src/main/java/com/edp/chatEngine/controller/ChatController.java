package com.edp.chatEngine.controller;

import com.edp.chatEngine.model.Message;
import com.edp.chatEngine.producer.ChatProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

	private final ChatProducer chatProducer;

	public ChatController(ChatProducer chatProducer){
		this.chatProducer = chatProducer;
	}

	@PostMapping("/private")
	public ResponseEntity<String> sendPrivateMessage(
		@RequestParam String sender,
		@RequestParam String receiver,
		@RequestParam String content
	) {
		Message message = new Message(sender, receiver, content, Message.MessageType.PRIVATE);
		chatProducer.sendPrivateMessage(message);
		return ResponseEntity.ok("Private message sent to queue!");
	}

	@PostMapping("/broadcast")
	public ResponseEntity<String> sendBroadcastMessage(
		@RequestParam String sender,
		@RequestParam String content
	) {
		Message message = new Message(sender, null, content, Message.MessageType.BROADCAST);
		chatProducer.sendBroadcastMessage(message);
		return ResponseEntity.ok("Broadcast message sent to queue!");
	}
}

