package arunx.bootcamp.kafka.controller;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestParam;

import arunx.bootcamp.kafka.controller.service.KafkaService;

public class KafkaController {

	private final KafkaService kafkaService;
	
	public KafkaController(KafkaService kafkaService) {
		this.kafkaService = kafkaService;
	}
	
	
	public String sendMessage(@RequestParam("message") String message) {
		kafkaService.sendMessage(message);
		return "Published successfully!";
	}
	
	@Bean
	public NewTopic adviceTopic() {
		return new NewTopic("Bootcamp", 3, (short) 1);
	}
	
	
}
