package com.arunx.bootcamp.reactive;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class ReactiveApplication {
	
	private static Logger log = LoggerFactory.getLogger(ReactiveApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReactiveApplication.class, args);
		
		/*
		System.out.println(">>===Stream==============");
		Stream<String> stream = Stream.of("red", "green", "blue");
		stream.map(String::toUpperCase).forEach(System.out::println);
		*/
		
		
		System.out.println("\n\n>>===Flux==============");
		Flux<String> flux = Flux.just("red", "green", "blue");
		
		Flux<String> upper = flux
				.log()
				.map(String::toUpperCase)
				.subscribeOn(Schedulers.newParallel("sub"))
				.publishOn(Schedulers.newParallel("pub"), 1);
		
		//upper.subscribe(value -> log.info("Consumed: " + value));
		upper.toStream();
	
	}
}
