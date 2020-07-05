package arunx.bootcamp.kafka.controller.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumer {

	@KafkaListener(topics = "Bootcamp")
	public void consumeMessage(String message) {
		log.info(String.format("*** Consumer received message -> %s", message));
	}
}
