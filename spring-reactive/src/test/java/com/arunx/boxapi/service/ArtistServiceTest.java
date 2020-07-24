package com.arunx.boxapi.service;

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
import org.springframework.web.server.ResponseStatusException;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.repository.ArtistRepository;
import com.arunx.boxapi.util.ArtistCreator;

import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class ArtistServiceTest {
  
  @InjectMocks
  private ArtistService service;
  
  @Mock
  private ArtistRepository repository;
  
  private final Artist artist = ArtistCreator.createValidArtist();
  
  @BeforeAll
  public static void blockHoundSetup() {
    BlockHound.install();
  }
  
  @BeforeEach
  public void setUp() {
    BDDMockito.when(repository.save(ArtistCreator.createArtistToBeSaved()))
      .thenReturn(Mono.just(artist));
    
    BDDMockito.when(repository.saveAll(List.of(ArtistCreator.createArtistToBeSaved(), ArtistCreator.createArtistToBeSaved())))
      .thenReturn(Flux.just(artist, artist));
    
     
    BDDMockito.when(repository.save(ArtistCreator.createUpdatedArtist()))
      .thenReturn(Mono.just(artist));

    
    BDDMockito.when(repository.delete(ArgumentMatchers.any(Artist.class)))
      .thenReturn(Mono.empty());

    BDDMockito.when(repository.findAll())
      .thenReturn(Flux.just(artist));
    
    BDDMockito.when(repository.findById(ArgumentMatchers.anyInt()))
      .thenReturn(Mono.just(artist));
  }
  
  @Test
  public void blockHoundWorks() {
      try {
          FutureTask<?> task = new FutureTask<>(() -> {
              Thread.sleep(0);
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
    
    StepVerifier.create(service.add(artistTobeSave))
      .expectSubscription()
      .expectNext(this.artist)
      .verifyComplete();
  }
  
  @Test
  @DisplayName("addAll")
  public void addAll() {
      Artist animeToBeSaved = ArtistCreator.createArtistToBeSaved();

      StepVerifier.create(service.addAll(List.of(animeToBeSaved, animeToBeSaved)))
          .expectSubscription()
          .expectNext(artist, artist)
          .verifyComplete();
  }
  
  @Test
  @DisplayName("addAllReturnErrorWhenInvalidName")
  public void addAllReturnErrorWhenInvalidName() {
      Artist artistToBeSaved = ArtistCreator.createArtistToBeSaved();

      BDDMockito.when(repository.saveAll(ArgumentMatchers.anyIterable()))
          .thenReturn(Flux.just(artist, artist.withName("")));

      StepVerifier.create(service.addAll(List.of(artistToBeSaved, artistToBeSaved.withName(""))))
          .expectSubscription()
          .expectNext(artist)
          .expectError(ResponseStatusException.class)
          .verify();
  }
  
  
  @Test
  @DisplayName("update")
  public void update() {
    StepVerifier.create(service.update(ArtistCreator.createUpdatedArtist()))
      .expectSubscription()
      .expectNext(this.artist)
      .verifyComplete();
  }
  
  @Test
  @DisplayName("updateReturnError")
  public void updateReturnError() {
    BDDMockito.when(repository.findById(ArgumentMatchers.anyInt()))
      .thenReturn(Mono.empty());

    StepVerifier.create(service.update(ArtistCreator.createUpdatedArtist()))
      .expectSubscription()
      .expectError(ResponseStatusException.class)
      .verify();
  }
  
  @Test
  @DisplayName("delete")
  public void delete() {
     StepVerifier.create(service.delete(1))
       .expectSubscription()
       .verifyComplete();
  }
  
  @Test
  @DisplayName("deleteReturnError")
  public void deleteReturnError() {
    BDDMockito.when(repository.findById(ArgumentMatchers.anyInt()))
      .thenReturn(Mono.empty());
    
    
     StepVerifier.create(service.delete(1))
       .expectSubscription()
       .expectError(ResponseStatusException.class)
       .verify();
  }
 
  @Test
  @DisplayName("findAll")
  public void findAll() {
     StepVerifier.create(service.findAll())
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
  
  @Test
  @DisplayName("findByIdReturnError")
  public void findByIdReturnError() {
    BDDMockito.when(repository.findById(ArgumentMatchers.anyInt()))
      .thenReturn(Mono.empty());
    
     StepVerifier.create(service.findById(1))
       .expectSubscription()
       .expectError(ResponseStatusException.class)
       .verify();
  }
  
  
}
