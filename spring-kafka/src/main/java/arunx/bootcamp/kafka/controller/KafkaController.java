package arunx.bootcamp.kafka.controller;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import arunx.bootcamp.kafka.controller.service.KafkaService;

@RestController
@RequestMapping("kafka")
public class KafkaController {

	private final KafkaService kafkaService;
	
	public KafkaController(KafkaService kafkaService) {
		this.kafkaService = kafkaService;
	}
	
	@PostMapping("/publish")
	public String sendMessage(@RequestParam("message") String message) {
		kafkaService.sendMessage(message);
		return "Published successfully!";
	}
	
	/*
	@Bean
	public NewTopic adviceTopic() {
		return new NewTopic("Bootcamp", 3, (short) 1);
	}
	*/
	
	
}
