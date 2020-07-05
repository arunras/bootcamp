package arunx.bootcamp.kafka.controller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaService {
	private static final String TOPIC = "Bootcamp";
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	public void sendMessage(String message) {
		log.info(String.format("*** Producer sending message -> %s", message));
		kafkaTemplate.send(TOPIC, message);
	}
}
