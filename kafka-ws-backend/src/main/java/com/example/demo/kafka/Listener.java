package com.example.demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import com.example.demo.model.FileModel;

@Component
public class Listener {

	private static final Logger LOG = LoggerFactory.getLogger(Listener.class);

	@Autowired
	private SimpMessagingTemplate webSocket;

	@KafkaListener(topics = {"messages", "pdf"})
	public void processMessage(ConsumerRecord<String, FileModel> cr, @Payload FileModel content) {

		LOG.info("Kafka published: {}", content);
		//Topic is always PDF because of FileModel.getRawFormat() but you can subscribe to other messages.
		this.webSocket.convertAndSend("/topic/" + cr.topic(), content);
	}

}
