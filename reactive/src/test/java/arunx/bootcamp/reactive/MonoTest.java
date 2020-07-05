package arunx.bootcamp.reactive;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoTest {

	
	public void monoSubscriber() {
		log.info("monoSubscriber {}", "=====================================");
		String name = "Arun Rasmey";
		Mono<String> mono = Mono.just(name).log();
		
		mono.subscribe(); 
		
		log.info("--------------------------------------");
		
		StepVerifier.create(mono)
			.expectNext(name)
			.verifyComplete();
		log.info("END monoSubscriber {}", "=================================");
	}
	
	
	public void monoSubscriberConsumer() {
		log.info("monoSubscriberConsumer {}", "=====================================");
		String name = "Arun Rasmey";
		Mono<String> mono = Mono.just(name).log();
		
		mono.subscribe(s -> log.info("Test2: {}", s));

		log.info("--------------------------------------");
		
		StepVerifier.create(mono)
			.expectNext(name)
			.verifyComplete();
		log.info("END monoSubscriberConsumer {}", "=====================================");
	}
	
	
	
	public void monoSubscriberConsumerError() {
		log.info("monoSubscriberConsumerError {}", "=====================================");
		String name = "Arun Rasmey";
		Mono<String> mono = Mono.just(name).log()
				.map(s -> { throw new RuntimeException("Test Error");});
		
		mono.subscribe(s -> log.info("Test3: {}", s), e -> log.error("Something bad happened"));
		//mono.subscribe(s -> log.info("Name: {}", s), Throwable::printStackTrace);
		
		log.info("--------------------------------------");
		
		StepVerifier.create(mono)
			.expectError(RuntimeException.class)
			.verify();
		log.info("END monoSubscriberConsumerError {}", "=====================================");
		
	}
	

	public void monoSubscriberConsumerErrorComplete() {
		log.info("START monoSubscriberConsumerErrorComplete {}", "=====================================");
		String name = "Arun Rasmey";
		Mono<String> mono = Mono.just(name).map(String::toUpperCase);
		
		mono.subscribe(s -> log.info("Test4 : {}", s),
				Throwable::printStackTrace,
				() -> log.info("FINISHED!"));
		
		log.info("--------------------------------------");
		
		StepVerifier.create(mono)
			.expectNext(name.toUpperCase())
			.verifyComplete();
		log.info("END monoSubscriberConsumerErrorComplete {}", "=====================================");
	}
	
	public void monoSubscriberConsumerSubsciption() {
		log.info("===START monoSubscriberCompleteSubsciption {}", "=====================================");
		String name = "Arun Rasmey";
		Mono<String> mono = Mono.just(name)
				.log()
				.map(String::toUpperCase);
		
		mono.subscribe(s -> log.info("Test5 : {}", s),
				Throwable::printStackTrace,
				() -> log.info("FINISHED!"),
				subscription -> subscription.request(5));
		
		log.info("--------------------------------------");
		/*
		StepVerifier.create(mono)
			.expectNext(name.toUpperCase())
			.verifyComplete();
		*/
		log.info("===END monoSubscriberCompleteSubsciption {}", "=====================================");
		
	}
	
	public void monoDoOnMethods() {
		log.info("===START monoDoOnMethods {}", "=====================================");
		String name = "Arun Rasmey";
		Mono<Object> mono = Mono.just(name)
				.log()
				.map(String::toUpperCase)
				.doOnSubscribe(s -> log.info("1. Subscribed"))
				.doOnRequest(s -> log.info("2. Requested"))
				.doOnNext(s -> log.info("3. Next"))
				.flatMap(s -> Mono.empty())
				.doOnNext(s -> log.info("3. Next"))
				.doOnSuccess(s -> log.info("4. Success"));
		
		
		mono.subscribe(s -> log.info("Test5 : {}", s),
				Throwable::printStackTrace,
				() -> log.info("FINISHED!"));
		
		
		log.info("===END monoDoOnMethods {}", "=====================================");
		
	}
	
	
	public void monoDoOnError() {
		log.info("===START monoDoOnError {}", "=====================================");
		Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception"))
			.doOnError(e -> log.error("Error message: {}", e.getMessage()))
			.doOnNext(s -> log.info("Execute doOnNext: {}", s))
			.log();
		
		StepVerifier.create(error)
			.expectError(IllegalArgumentException.class)
			.verify();
		
		
		log.info("===END monoDoOnError {}", "=====================================");
	}
	
	
	public void monoDoOnErrorResume() {
		log.info("===START monoDoOnError {}", "=====================================");
		String name = "Arun Rasmey";
		Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception"))
			.onErrorResume(s -> {
				log.info("Inside onErrorResume");
				return Mono.just(name);
			})
			.doOnError(e -> log.error("Error message: {}", e.getMessage()))
			.log();
		
		StepVerifier.create(error)
			.expectNext(name)
			.verifyComplete();
		
		
		log.info("===END monoDoOnError {}", "=====================================");
	}
	
	@Test
	public void monoDoOnErrorReturn() {
		log.info("===START monoDoOnError {}", "=====================================");
		String name = "Arun Rasmey";
		Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception"))
			.onErrorReturn("EMPTY")
			.onErrorResume(s -> {
				log.info("Inside onErrorResume");
				return Mono.just(name);
			})
			.doOnError(e -> log.error("Error message: {}", e.getMessage()))
			.log();
		
		StepVerifier.create(error)
			.expectNext("EMPTY")
			.verifyComplete();
		
		
		log.info("===END monoDoOnError {}", "=====================================");
	}
}
