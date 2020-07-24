package com.arunx.boxapi.integration;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.repository.ArtistRepository;
import com.arunx.boxapi.util.ArtistCreator;
import com.arunx.boxapi.util.WebTestClientUtil;

import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
/*
@ExtendWith(SpringExtension.class)
@WebFluxTest
@Import({ArtistService.class, CustomAttributes.class})
*/
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ArtistControllerIT {
  @Autowired
  private WebTestClientUtil webTestClientUtil;

  @MockBean
  private ArtistRepository repository;

  private WebTestClient testClientUser;
  private WebTestClient testClientAdmin;
  private WebTestClient testClientInvalid;

  private final Artist artist = ArtistCreator.createValidArtist();
  @BeforeAll
  public static void blockHoundSetup() {
      BlockHound.install(
          builder -> builder.allowBlockingCallsInside("java.util.UUID", "randomUUID")
      );
  }
  
  @BeforeEach
  public void setUp() {
    testClientUser = webTestClientUtil.authenticateClient("user", "test");
    testClientAdmin = webTestClientUtil.authenticateClient("admin", "test");
    testClientInvalid = webTestClientUtil.authenticateClient("xxx", "xxx");
    
    BDDMockito.when(repository.save(ArtistCreator.createArtistToBeSaved()))
        .thenReturn(Mono.just(artist));
    
    BDDMockito.when(repository.saveAll(List.of(ArtistCreator.createArtistToBeSaved(), ArtistCreator.createArtistToBeSaved())))
        .thenReturn(Flux.just(artist, artist));
        
    BDDMockito.when(repository.save(ArtistCreator.createValidArtist()))
        .thenReturn(Mono.just(artist));
    
    BDDMockito.when(repository.delete(ArgumentMatchers.any(Artist.class)))
        .thenReturn(Mono.empty());
    
    BDDMockito.when(repository.findById(ArgumentMatchers.anyInt()))
        .thenReturn(Mono.just(artist));

    BDDMockito.when(repository.findAll())
        .thenReturn(Flux.just(artist));
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
    Artist artistToBeSaved = ArtistCreator.createArtistToBeSaved();
    testClientUser.post()
              .uri("/artists/add")
              .contentType(MediaType.APPLICATION_JSON)
              .body(BodyInserters.fromValue(artistToBeSaved))
              .exchange()
              .expectStatus().isCreated()
              .expectBody(Artist.class)
              .isEqualTo(artist);
  }
  
  @Test
  @DisplayName("addWhenNameIsEmpty")
  public void addWhenNameIsEmpty() {
    Artist artistToBeSaved = ArtistCreator.createArtistToBeSaved().withName("");
    testClientUser.post()
              .uri("/artists/add")
              .contentType(MediaType.APPLICATION_JSON)
              .body(BodyInserters.fromValue(artistToBeSaved))
              .exchange()
              .expectStatus().isBadRequest()
              .expectBody()
              .jsonPath("$.status").isEqualTo(400);
  }
  
  @Test
  @DisplayName("saveBatch creates a list of anime when successful")
  public void saveBatch_CreatesListOfAnime_WhenSuccessful() {
    Artist artistToBeSaved = ArtistCreator.createArtistToBeSaved();

    testClientUser.post()
              .uri("/artists/addBatch")
              .contentType(MediaType.APPLICATION_JSON)
              .body(BodyInserters.fromValue(List.of(artistToBeSaved, artistToBeSaved)))
              .exchange()
              .expectStatus().isCreated()
              .expectBodyList(Artist.class)
              .hasSize(2)
              .contains(artist);
  }
  
  @Test
  @DisplayName("addBatchReturnErrorWhenNameIsEmpty")
  public void addBatchReturnErrorWhenNameIsEmpty() {
    Artist artistToBeSaved = ArtistCreator.createArtistToBeSaved(); 

    BDDMockito.when(repository.saveAll(ArgumentMatchers.anyIterable()))
        .thenReturn(Flux.just(artist, artist.withName("")));

    testClientUser.post()
              .uri("/artists/addBatch")
              .contentType(MediaType.APPLICATION_JSON)
              .body(BodyInserters.fromValue(List.of(artistToBeSaved, artistToBeSaved)))
              .exchange()
              .expectStatus().isBadRequest()
              .expectBody()
              .jsonPath("$.status").isEqualTo(400);
  }

  
  @Test
  @DisplayName("update")
  public void update() {
    testClientUser.put()
              .uri("/artists/update")
              .contentType(MediaType.APPLICATION_JSON)
              .body(BodyInserters.fromValue(artist))
              .exchange()
              .expectStatus().isCreated();
  }
  
  @Test
  @DisplayName("updateMonoErrorWhenArtistDoesNotExist")
  public void updateMonoErrorWhenArtistDoesNotExist() {
      BDDMockito.when(repository.findById(ArgumentMatchers.anyInt()))
          .thenReturn(Mono.empty());

      testClientUser.put()
              .uri("/animes/update")
              .contentType(MediaType.APPLICATION_JSON)
              .body(BodyInserters.fromValue(artist))
              .exchange()
              .expectStatus().isNotFound()
              .expectBody()
              .jsonPath("$.status").isEqualTo(404)
              .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
  }
  
  @Test
  @DisplayName("delete")
  public void delete() {
    testClientUser.delete()
              .uri("/artists/delete/{id}", 1)
              .exchange()
              .expectStatus().isNoContent();
  }
  
  @Test
  @DisplayName("delete returns Mono error when anome does not exist")
  public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
    BDDMockito.when(repository.findById(ArgumentMatchers.anyInt()))
        .thenReturn(Mono.empty());

    testClientUser.delete()
              .uri("/artists/{id}", 1)
              .exchange()
              .expectStatus().isNotFound()
              .expectBody()
              .jsonPath("$.status").isEqualTo(404)
              .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
  }
  
  @Test
  @DisplayName("findById")
  public void findById() {
    testClientUser.get()
              .uri("/artists/find/{id}", 1)
              .exchange()
              .expectStatus().isOk()
              .expectBody(Artist.class)
              .isEqualTo(artist);

  }
  
  @Test
  @DisplayName("findByIdReturnEmpty")
  public void findByIdReturnEmpty() {
    testClientUser.get()
              .uri("/artists/find{id}", 1)
              .exchange()
              .expectStatus().isNotFound()
              .expectBody()
              .jsonPath("$.status").isEqualTo(404)
              .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
  }
  
  @Test
  @DisplayName("listAll")
  public void listAll() {
    testClientAdmin.get()
              .uri("/artists/list")
              .exchange()
              .expectStatus().is2xxSuccessful()
              .expectBody()
              .jsonPath("$.[0].id").isEqualTo(artist.getId())
              .jsonPath("$.[0].name").isEqualTo(artist.getName());
  }
  
  @Test
  @DisplayName("listAllWithListSize")
  public void listAllWithListSize() {
    testClientUser.get()
              .uri("/artists/list")
              .exchange()
              .expectStatus().isOk()
              .expectBodyList(Artist.class)
              .hasSize(1)
              .contains(artist);
  }
  
  
  
  
}
