package com.arunx.boxapi.integration;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.respository.ArtistRepository;
import com.arunx.boxapi.util.ArtistCreator;
import com.arunx.boxapi.wrapper.PageableResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ArtistIT {
  @Autowired
  @Qualifier(value = "testRestTemplateRoleUser")
  private TestRestTemplate restTemplateRoleUser;
  
  @Autowired
  @Qualifier(value = "testRestTemplateRoleAdmin")
  private TestRestTemplate restTemplateRoleAdmin;
  
  /*
  @LocalServerPort
  private int port;
  */
  
  @MockBean
  private ArtistRepository repository;
  
  @Lazy
  @TestConfiguration
  static class Config {
    
    @Bean(name = "testRestTemplateRoleUser")
    public TestRestTemplate testRestTemplateRoleUser(@Value("${local.server.port}") int port) {
      RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
          .rootUri("http://localhost:" + port)
          .basicAuthentication("user", "test");
      
      return new TestRestTemplate(restTemplateBuilder);
    }
    
    @Bean(name = "testRestTemplateRoleAdmin")
    public TestRestTemplate testRestTemplateRoleAdmin(@Value("${local.server.port}") int port) {
      RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
          .rootUri("http://localhost:" + port)
          .basicAuthentication("admin", "test");
      
      return new TestRestTemplate(restTemplateBuilder);
    }

  }
  
  
  private String uri = "/artists";
  
  
  @BeforeEach
  public void setUp() {
    BDDMockito.when(repository.save(ArtistCreator.createArtistToBeAdded()))
      .thenReturn(ArtistCreator.createValidArtist());

    BDDMockito.when(repository.save(ArtistCreator.createValidArtist()))
      .thenReturn(ArtistCreator.createValidUpdatedArtist());

    BDDMockito.doNothing().when(repository).delete(ArgumentMatchers.any(Artist.class));

       
    BDDMockito.when(repository.findById(ArgumentMatchers.anyInt()))
      .thenReturn(Optional.of(ArtistCreator.createValidArtist()));

    BDDMockito.when(repository.findByName(ArgumentMatchers.anyString()))
      .thenReturn(List.of(ArtistCreator.createValidArtist()));
    
    PageImpl<Artist> page = new PageImpl<>(List.of(ArtistCreator.createValidArtist()));
    BDDMockito.when(repository.findAll(ArgumentMatchers.any(PageRequest.class)))
      .thenReturn(page);
  }
  
  @Test
  @DisplayName("add")
  public void add() {
    Integer expectedId = ArtistCreator.createValidArtist().getId();
    
    Artist artistToBeAdded = ArtistCreator.createArtistToBeAdded();
    
    Artist artist = restTemplateRoleUser.exchange(uri + "/add", HttpMethod.POST,
        createJsonHttpEntity(artistToBeAdded), Artist.class).getBody();

    Assertions.assertThat(artist).isNotNull();
    Assertions.assertThat(artist.getId()).isNotNull();
    Assertions.assertThat(artist.getId()).isEqualTo(expectedId);
  }
  
  @Test
  @DisplayName("update")
  public void update() {
    Artist artist = ArtistCreator.createValidArtist();
    
    ResponseEntity<Artist> response = restTemplateRoleUser.exchange(uri + "/update", HttpMethod.PUT,
        createJsonHttpEntity(artist), Artist.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
  }
  
  @Test
  @DisplayName("delete")
  public void delete() {
    ResponseEntity<Void> response = restTemplateRoleAdmin.exchange(uri + "/admin/delete/1", HttpMethod.DELETE,
        null, Void.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    Assertions.assertThat(response.getBody()).isNull();
  }
  
  @Test
  @DisplayName("deleteReturn403WhenUserNotAdmin")
  public void deleteReturn403WhenUserNotAdmin() {
    ResponseEntity<Void> response = restTemplateRoleUser.exchange(uri + "/admin/delete/1", HttpMethod.DELETE,
        null, Void.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }
  
  @Test
  @DisplayName("findById")
  public void findById() {
    Integer expectedId = ArtistCreator.createValidArtist().getId();
    
    Artist artist = restTemplateRoleUser.getForObject(uri + "/find/1", Artist.class);
    
    Assertions.assertThat(artist).isNotNull();
    Assertions.assertThat(artist.getId()).isNotNull();
    Assertions.assertThat(artist.getId()).isEqualTo(expectedId);
  }
  
  @Test
  @DisplayName("findByName")
  public void findByName() {
    String expectedName = ArtistCreator.createValidArtist().getName();
    
    List<Artist> list = restTemplateRoleUser.exchange(uri + "/find?name='A'", HttpMethod.GET,
        null, new ParameterizedTypeReference<List<Artist>> () {}).getBody();
    
    Assertions.assertThat(list).isNotNull();
    Assertions.assertThat(list).isNotNull();
    Assertions.assertThat(list.get(0).getName()).isEqualTo(expectedName);
  }
  
  @Test
  @DisplayName("listAllPage")
  public void listAllPage() {
    String expectedName = ArtistCreator.createValidArtist().getName();
    
    Page<Artist> page = restTemplateRoleUser.exchange(uri + "/pages", HttpMethod.GET,
        null, new ParameterizedTypeReference<PageableResponse<Artist>>() {}).getBody();
    
    Assertions.assertThat(page).isNotNull();
    Assertions.assertThat(page.toList()).isNotEmpty();
    Assertions.assertThat(page.toList().get(0).getName()).isEqualTo(expectedName);
  }
  
  private static HttpHeaders createJsonHeader() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    return httpHeaders;
  }
  
  private HttpEntity<Artist> createJsonHttpEntity(Artist artist) {
    return new HttpEntity<>(artist, createJsonHeader());
  }

}
