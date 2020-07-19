package com.arunx.boxapi.service;

import java.util.List;
import java.util.Optional;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.exception.ResourceNotFoundException;
import com.arunx.boxapi.respository.ArtistRepository;
import com.arunx.boxapi.util.ArtistCreator;
import com.arunx.boxapi.util.Utils;

@ExtendWith(SpringExtension.class)
class ArtistServiceTest {
  @InjectMocks
  private ArtistService service;
  
  @Mock
  private ArtistRepository repository;
  
  @Mock
  private Utils utils;
  
  
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
  
    BDDMockito.when(utils.findArtistOrThrowNotFound(ArgumentMatchers.anyInt(), ArgumentMatchers.any(ArtistRepository.class)))
      .thenReturn(ArtistCreator.createValidArtist());
  }
  
  @Test
  @DisplayName("add")
  public void add() {
    Integer expectedId = ArtistCreator.createValidArtist().getId();
    
    Artist artistToBeAdded = ArtistCreator.createArtistToBeAdded();
    
    Artist artist = service.add(artistToBeAdded);

    Assertions.assertThat(artist).isNotNull();
    Assertions.assertThat(artist.getId()).isNotNull();
    Assertions.assertThat(artist.getId()).isEqualTo(expectedId);
  }
  
  @Test
  @DisplayName("update")
  public void update() {
    Artist updatedArtist = ArtistCreator.createValidUpdatedArtist();
    
    String expectedName = updatedArtist.getName();
    
    Artist artist = service.update(ArtistCreator.createValidArtist());

    Assertions.assertThat(artist).isNotNull();
    Assertions.assertThat(artist.getId()).isNotNull();
    Assertions.assertThat(artist.getName()).isEqualTo(expectedName);
  }
  
  @Test
  @DisplayName("delete")
  public void delete() {
    Assertions.assertThatCode(() -> service.delete(1))
      .doesNotThrowAnyException();
  }
  
  @Test
  @DisplayName("deleteThrowResourceNotFoundExceptionWhenArtistDoesNotExist")
  public void deleteThrowResourceNotFoundExceptionWhenArtistDoesNotExist() {
    BDDMockito.when(utils.findArtistOrThrowNotFound(ArgumentMatchers.anyInt(), ArgumentMatchers.any(ArtistRepository.class)))
      .thenThrow(new ResourceNotFoundException("Artist no found"));
    
    Assertions.assertThatExceptionOfType(ResourceNotFoundException.class)
      .isThrownBy(() -> service.delete(1));
  }
  
  @Test
  @DisplayName("findById")
  public void findById() {
    Integer expectedId = ArtistCreator.createValidArtist().getId();
    
    Artist artist = service.findById(1);
    
    Assertions.assertThat(artist).isNotNull();
    Assertions.assertThat(artist.getId()).isNotNull();
    Assertions.assertThat(artist.getId()).isEqualTo(expectedId);
  }
  
  @Test
  @DisplayName("findByName")
  public void findByName() {
    String expectedName = ArtistCreator.createValidArtist().getName();
    
    List<Artist> list = service.findByName("AB");
    
    Assertions.assertThat(list).isNotNull();
    Assertions.assertThat(list).isNotNull();
    Assertions.assertThat(list.get(0).getName()).isEqualTo(expectedName);
  }
  
  @Test
  @DisplayName("listAllPage")
  public void listAllPage() {
    String expectedName = ArtistCreator.createValidArtist().getName();
    
    Page<Artist> page = service.listAllPage(PageRequest.of(1, 1));
    
    Assertions.assertThat(page).isNotNull();
    Assertions.assertThat(page.toList()).isNotEmpty();
    Assertions.assertThat(page.toList().get(0).getName()).isEqualTo(expectedName);
  }

}
