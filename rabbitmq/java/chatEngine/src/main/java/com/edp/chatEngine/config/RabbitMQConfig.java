package com.edp.chatEngine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	public static final String CHAT_QUEUE = "chat.queue";
	public static final String CHAT_EXCHANGE = "chat.exchange";
	public static final String CHAT_ROUTING_KEY = "chat.routing.key";
	public static final String DLQ_QUEUE = "chat.dlq";
	public static final String DLQ_EXCHANGE = "chat.dlq.exchange";
	public static final String DLQ_ROUTING_KEY = "chat.dlq.routing.key";
	public static final String BROADCAST_QUEUE = "chat.broadcast.queue";
	public static final String BROADCAST_EXCHANGE = "chat.broadcast.exchange";

	@Bean
	public Queue chatQueue() {
		return QueueBuilder.durable(CHAT_QUEUE)
		.withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
		.withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
		.build();
	}

	@Bean
	public DirectExchange chatExchange() {
		return new DirectExchange(CHAT_EXCHANGE);
	}

	@Bean
	public Binding chatBinding(Queue chatQueue, DirectExchange chatExchange) {
		return BindingBuilder
			.bind(chatQueue)
			.to(chatExchange)
			.with(CHAT_ROUTING_KEY);
	}

	@Bean
	public Queue broadcastQueue() {
		return QueueBuilder.durable(BROADCAST_QUEUE).build();
	}

	@Bean
	public FanoutExchange broadcastExchange() {
		return new FanoutExchange(BROADCAST_EXCHANGE);
	}

	@Bean
	public Binding broadcastBinding(Queue broadcastQueue, FanoutExchange broadcastExchange) {
		return BindingBuilder
				.bind(broadcastQueue)
				.to(broadcastExchange);
	}
	
	@Bean
	public Queue dlqQueue() {
		return QueueBuilder.durable(DLQ_QUEUE).build();
	}

	@Bean
	public DirectExchange dlqExchange() {
		return new DirectExchange(DLQ_EXCHANGE);
	}

	@Bean
	public Binding dlqBinding(Queue dlqQueue, DirectExchange dlqExchange) {
		return BindingBuilder
				.bind(dlqQueue)
				.to(dlqExchange)
				.with(DLQ_ROUTING_KEY);
	}
	
	@Bean
	public Jackson2JsonMessageConverter messageConverter() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return new Jackson2JsonMessageConverter(objectMapper);
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter());
		return template;
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
			ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(messageConverter());
		return factory;
	}
}