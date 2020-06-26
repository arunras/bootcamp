package arunx.bootcamp.reactive;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoTest {

	@Test
	public void monoSubscriber() {
		String name = "Arun Rasmey";
		Mono<String> mono = Mono.just(name).log();
		
		mono.subscribe();
		
		log.info("=====================================");
		
		StepVerifier.create(mono)
			.expectNext(name)
			.verifyComplete();
		
	}
	
	@Test
	public void monoSubscriberConsumer() {
		String name = "Arun Rasmey";
		Mono<String> mono = Mono.just(name).log();
		
		mono.subscribe(s -> log.info("Test2: {}", s));
		
		log.info("=====================================");
		
		StepVerifier.create(mono)
			.expectNext(name)
			.verifyComplete();
		
	}
	
	@Test
	public void monoSubscriberConsumerError() {
		String name = "Arun Rasmey";
		Mono<String> mono = Mono.just(name)
				.map(s -> { throw new RuntimeException("Test Error");});
		
		mono.subscribe(s -> log.info("Test3: {}", s), e -> log.error("Something bad happened"));
		//mono.subscribe(s -> log.info("Name: {}", s), Throwable::printStackTrace);
		
		log.info("=====================================");
		
		StepVerifier.create(mono)
			.expectError(RuntimeException.class)
			.verify();
		
	}
	
	@Test
	public void monoSubscriberComplete() {
		String name = "Arun Rasmey";
		Mono<String> mono = Mono.just(name)
				.map(String::toUpperCase);
		
		mono.subscribe(s -> log.info("Test4 : {}", s),
				Throwable::printStackTrace,
				() -> log.info("FINISHED!"));
		
		log.info("=====================================");
		
		StepVerifier.create(mono)
			.expectNext(name.toUpperCase())
			.verifyComplete();
		
	}
	
	@Test
	public void monoSubscriberCompleteSubsciption() {
		String name = "Arun Rasmey";
		Mono<String> mono = Mono.just(name)
				.map(String::toUpperCase);
		
		mono.subscribe(s -> log.info("Test5 : {}", s),
				Throwable::printStackTrace,
				() -> log.warn("FINISHED!"),
				Subscription::cancel);
		
		log.info("=====================================");
		
		StepVerifier.create(mono)
			.expectNext(name.toUpperCase())
			.verifyComplete();
		
	}
}
