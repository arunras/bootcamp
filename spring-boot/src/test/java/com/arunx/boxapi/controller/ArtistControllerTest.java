package com.arunx.boxapi.controller;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.service.ArtistService;
import com.arunx.boxapi.util.ArtistCreator;

//@SpringBootTest
@ExtendWith(SpringExtension.class)
class ArtistControllerTest {
  @InjectMocks
  private ArtistController controller;
  
  @Mock
  private ArtistService service;
  
  
  @BeforeEach
  public void setUp() {
    BDDMockito.when(service.add(ArtistCreator.createArtistToBeAdded()))
      .thenReturn(ArtistCreator.createValidArtist());

    BDDMockito.when(service.update(ArtistCreator.createValidArtist()))
      .thenReturn(ArtistCreator.createValidUpdatedArtist());

    BDDMockito.doNothing().when(service).delete(ArgumentMatchers.anyInt());

       
    BDDMockito.when(service.findById(ArgumentMatchers.anyInt()))
      .thenReturn(ArtistCreator.createValidArtist());

    BDDMockito.when(service.findByName(ArgumentMatchers.anyString()))
      .thenReturn(List.of(ArtistCreator.createValidArtist()));
    
    PageImpl<Artist> page = new PageImpl<>(List.of(ArtistCreator.createValidArtist()));
    BDDMockito.when(service.listAllPage(ArgumentMatchers.any()))
      .thenReturn(page);
  }
  
  @Test
  @DisplayName("add")
  public void add() {
    Integer expectedId = ArtistCreator.createValidArtist().getId();
    
    Artist artistToBeAdded = ArtistCreator.createArtistToBeAdded();
    
    Artist artist = controller.add(artistToBeAdded).getBody();

    Assertions.assertThat(artist).isNotNull();
    Assertions.assertThat(artist.getId()).isNotNull();
    Assertions.assertThat(artist.getId()).isEqualTo(expectedId);
  }
  
  @Test
  @DisplayName("update")
  public void update() {
    ResponseEntity<Artist> response = controller.update(ArtistCreator.createValidArtist());

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
  }
  
  @Test
  @DisplayName("delete")
  public void delete() {
    ResponseEntity<Void> response = controller.delete(1);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    Assertions.assertThat(response.getBody()).isNull();
  }
  
  @Test
  @DisplayName("findById")
  public void findById() {
    Integer expectedId = ArtistCreator.createValidArtist().getId();
    
    Artist artist = controller.findById(1, null).getBody();
    
    Assertions.assertThat(artist).isNotNull();
    Assertions.assertThat(artist.getId()).isNotNull();
    Assertions.assertThat(artist.getId()).isEqualTo(expectedId);
  }
  
  @Test
  @DisplayName("findByName")
  public void findByName() {
    String expectedName = ArtistCreator.createValidArtist().getName();
    
    List<Artist> list = controller.findByName("AB").getBody();
    
    Assertions.assertThat(list).isNotNull();
    Assertions.assertThat(list).isNotNull();
    Assertions.assertThat(list.get(0).getName()).isEqualTo(expectedName);
  }
  
  @Test
  @DisplayName("listAllPage")
  public void listAllPage() {
    String expectedName = ArtistCreator.createValidArtist().getName();
    
    Page<Artist> page = controller.listAllPage(null).getBody();
    
    Assertions.assertThat(page).isNotNull();
    Assertions.assertThat(page.toList()).isNotEmpty();
    Assertions.assertThat(page.toList().get(0).getName()).isEqualTo(expectedName);
  }

}
