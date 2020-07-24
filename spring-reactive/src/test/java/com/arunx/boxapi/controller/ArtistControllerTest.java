package com.arunx.boxapi.controller;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.service.ArtistService;
import com.arunx.boxapi.util.ArtistCreator;

import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class ArtistControllerTest {
  
  @InjectMocks
  private ArtistController controller;
  
  @Mock
  private ArtistService service;
  
  private final Artist artist = ArtistCreator.createValidArtist();
  
  @BeforeAll
  public static void blockHoundSetup() {
      BlockHound.install();
  }
  
  @BeforeEach
  public void setUp() {
    
    BDDMockito.when(service.add(ArtistCreator.createArtistToBeSaved()))
      .thenReturn(Mono.just(artist));
     
    BDDMockito.when(service.update(ArtistCreator.createValidArtist()))
      .thenReturn(Mono.just(artist));
    
    BDDMockito.when(service.delete(ArgumentMatchers.anyInt()))
      .thenReturn(Mono.empty());

    BDDMockito.when(service
        .addAll(List.of(ArtistCreator.createArtistToBeSaved(), ArtistCreator.createArtistToBeSaved())))
        .thenReturn(Flux.just(artist, artist));
    
    BDDMockito.when(service.findAll())
      .thenReturn(Flux.just(artist));
    
    BDDMockito.when(service.findById(ArgumentMatchers.anyInt()))
      .thenReturn(Mono.just(artist));
  }
  
  @Test
  public void blockHoundWorks() {
      try {
          FutureTask<?> task = new FutureTask<>(() -> {
              Thread.sleep(0); //NOSONAR
              return "";
          });
          Schedulers.parallel().schedule(task);

          task.get(10, TimeUnit.SECONDS);
          Assertions.fail("should fail");
      } catch (Exception e) {
          Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
      }
  }
  
  @Test
  @DisplayName("add")
  public void add() {
    Artist artistTobeSave = ArtistCreator.createArtistToBeSaved();
    
    StepVerifier.create(controller.add(artistTobeSave))
      .expectSubscription()
      .expectNext(this.artist)
      .verifyComplete();
  }
  
  @Test
  @DisplayName("addBatch")
  public void addBatch() {
      Artist artistToBeSaved = ArtistCreator.createArtistToBeSaved();

      StepVerifier.create(controller.addBatch(List.of(artistToBeSaved, artistToBeSaved)))
          .expectSubscription()
          .expectNext(artist, artist)
          .verifyComplete();
  }
  
  
  @Test
  @DisplayName("update")
  public void update() {
    StepVerifier.create(controller.update(ArtistCreator.createValidArtist()))
      .expectSubscription()
      .expectNext(this.artist)
      .verifyComplete();
  }
  
  @Test
  @DisplayName("delete")
  public void delete() {
     StepVerifier.create(controller.delete(1))
       .expectSubscription()
       .verifyComplete();
  }
 
  @Test
  @DisplayName("findAll")
  public void findAll() {
     StepVerifier.create(controller.listAll())
       .expectSubscription()
       .expectNext(this.artist)
       .verifyComplete();
  }
  
  @Test
  @DisplayName("findById")
  public void findById() {
     StepVerifier.create(service.findById(1))
       .expectSubscription()
       .expectNext(this.artist)
       .verifyComplete();
  }
  
}
