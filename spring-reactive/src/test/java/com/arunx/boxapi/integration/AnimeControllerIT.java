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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.repository.ArtistRepository;
import com.arunx.boxapi.util.ArtistCreator;

import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AnimeControllerIT {
    private static final String ADMIN_USER = "user";
    private static final String REGULAR_USER = "admin";

    @MockBean
    private ArtistRepository animeRepositoryMock;

    @Autowired
    private WebTestClient client;

    private final Artist artist = ArtistCreator.createValidArtist();

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install(
            builder -> builder.allowBlockingCallsInside("java.util.UUID", "randomUUID")
        );
    }

    @BeforeEach
    public void setUp() {
        BDDMockito.when(animeRepositoryMock.findAll())
            .thenReturn(Flux.just(artist));

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
            .thenReturn(Mono.just(artist));

        BDDMockito.when(animeRepositoryMock.save(ArtistCreator.createArtistToBeSaved()))
            .thenReturn(Mono.just(artist));

        BDDMockito.when(animeRepositoryMock
            .saveAll(List.of(ArtistCreator.createArtistToBeSaved(), ArtistCreator.createArtistToBeSaved())))
            .thenReturn(Flux.just(artist, artist));

        BDDMockito.when(animeRepositoryMock.delete(ArgumentMatchers.any(Artist.class)))
            .thenReturn(Mono.empty());

        BDDMockito.when(animeRepositoryMock.save(ArtistCreator.createValidArtist()))
            .thenReturn(Mono.empty());

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
    @DisplayName("listAll returns unauthorized when user is not authenticated")
    public void listAll_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
        client
            .get()
            .uri("/animes")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("listAll returns forbidden when user is successfully authenticated and does not have role ADMIN")
    @WithUserDetails(REGULAR_USER)
    public void listAll_ReturnForbidden_WhenUserDoesNotHaveRoleAdmin() {
        client
            .get()
            .uri("/animes")
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("listAll returns a flux of artist when user is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void listAll_ReturnFluxOfAnime_WhenSuccessful() {
        client
            .get()
            .uri("/animes")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody()
            .jsonPath("$.[0].id").isEqualTo(artist.getId())
            .jsonPath("$.[0].name").isEqualTo(artist.getName());
    }

    @Test
    @DisplayName("listAll returns a flux of artist when user is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void listAll_Flavor2_ReturnFluxOfAnime_WhenSuccessful() {
        client
            .get()
            .uri("/animes")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Artist.class)
            .hasSize(1)
            .contains(artist);
    }

    @Test
    @DisplayName("findById returns a Mono with artist when it exists and user is successfully authenticated and has role USER")
    @WithUserDetails(REGULAR_USER)
    public void findById_ReturnMonoAnime_WhenSuccessful() {
        client
            .get()
            .uri("/animes/{id}", 1)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Artist.class)
            .isEqualTo(artist);
    }

    @Test
    @DisplayName("findById returns Mono error when artist does not exist and user is successfully authenticated and has role USER")
    @WithUserDetails(REGULAR_USER)
    public void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
            .thenReturn(Mono.empty());

        client
            .get()
            .uri("/animes/{id}", 1)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("save creates an artist when successful and user is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void save_CreatesAnime_WhenSuccessful() {
        Artist animeToBeSaved = ArtistCreator.createArtistToBeSaved();

        client
            .post()
            .uri("/animes")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(animeToBeSaved))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Artist.class)
            .isEqualTo(artist);
    }

    @Test
    @DisplayName("saveBatch creates a list of artist when successful and user is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void saveBatch_CreatesListOfAnime_WhenSuccessful() {
        Artist animeToBeSaved = ArtistCreator.createArtistToBeSaved();

        client
            .post()
            .uri("/animes/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved)))
            .exchange()
            .expectStatus().isCreated()
            .expectBodyList(Artist.class)
            .hasSize(2)
            .contains(artist);
    }

    @Test
    @DisplayName("saveBatch returns Mono error when one of the objects in the list contains empty or null name and user is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void saveBatch_ReturnsMonoError_WhenContainsInvalidName() {
        Artist animeToBeSaved = ArtistCreator.createArtistToBeSaved();

        BDDMockito.when(animeRepositoryMock
            .saveAll(ArgumentMatchers.anyIterable()))
            .thenReturn(Flux.just(artist, artist.withName("")));

        client
            .post()
            .uri("/animes/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved)))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    @DisplayName("save returns mono error with bad request when name is empty and user is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void save_ReturnsError_WhenNameIsEmpty() {
        Artist animeToBeSaved = ArtistCreator.createArtistToBeSaved().withName("");

        client
            .post()
            .uri("/animes")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(animeToBeSaved))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.status").isEqualTo(400);

    }

    @Test
    @DisplayName("delete removes the artist when successful and user is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void delete_RemovesAnime_WhenSuccessful() {
        client
            .delete()
            .uri("/animes/{id}", 1)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("delete returns Mono error when artist does not exist and user is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
            .thenReturn(Mono.empty());

        client
            .delete()
            .uri("/animes/{id}", 1)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("update save updated artist and returns empty mono when successful and user is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void update_SaveUpdatedAnime_WhenSuccessful() {
        client
            .put()
            .uri("/animes/{id}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(artist))
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("update returns Mono error when artist does not exist and user is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void update_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
            .thenReturn(Mono.empty());

        client.put()
            .uri("/animes/{id}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(artist))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }
}
