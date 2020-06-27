package arunx.bootcamp.reactive;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
public class FluxTest {
	
	public void fluxSubscriber() {
		Flux<String> flux = Flux.just("A", "B", "C")
				.log();
		
		StepVerifier.create(flux)
			.expectNext("A", "B", "C")
			.verifyComplete();
	}
	
	public void fluxSubscriberNumbers() {
		Flux<Integer> flux = Flux.range(1, 5)
				.log();
		flux.subscribe(s -> log.info("Number: {}", s));
		
		log.info("--------------------------------------");
		
		StepVerifier.create(flux)
			.expectNext(1, 2, 3, 4, 5)
			.verifyComplete();
	}
	
	public void fluxSubscriberNumbersFromList() {
		Flux<Integer> flux = Flux.fromIterable(List.of(1, 2, 3, 4, 5))
				.log();
		flux.subscribe(i -> log.info("Number: {}", i));
		
		log.info("--------------------------------------");
		
		StepVerifier.create(flux)
			.expectNext(1, 2, 3, 4)
			.verifyComplete();
	}
	
	public void fluxSubscriberNumbersError() {
		Flux<Integer> flux = Flux.range(1, 5)
				.log()
				.map(i -> {
					if (i == 4) throw new IndexOutOfBoundsException("Index error");
					return i;
				});

		flux.subscribe(i -> log.info("Number: {}", i), 
				Throwable::printStackTrace,
				() -> log.info("DONE!"),
				subscription -> subscription.request(3));
		
		log.info("--------------------------------------");
		
		StepVerifier.create(flux)
			.expectNext(1, 2, 3)
			.expectError(IndexOutOfBoundsException.class)
			.verify();
	}

	@Test
	public void fluxSubscriberNumbersUglyBackpressure() {
		Flux<Integer> flux = Flux.range(1, 10)
				.log();

		flux.subscribe(new Subscriber<Integer>() {
			private int count = 0;
			private Subscription subscription;
			private int requestNumber = 2;

			@Override
			public void onSubscribe(Subscription s) {
				this.subscription = s;
				subscription.request(requestNumber);
			}

			@Override
			public void onNext(Integer t) {
				count++;
				if (count >= requestNumber) {
					count = 0;
					subscription.request(requestNumber);
				}
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			}
			
		});


		log.info("--------------------------------------");
		
		StepVerifier.create(flux)
			.expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
			.verifyComplete();
	}
}
