package com.arunx.boxapi.respository;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.util.ArtistCreator;

//@ExtendWith(SpringExtension.class)
@DataJpaTest
@DisplayName("ArtistRespository TESTs")
class ArtistRepositoryTest {
  
  @Autowired
  private ArtistRepository repository;
  
  @Test
  @DisplayName("addArtist")
  public void addArtist() {
    Artist artist = ArtistCreator.createArtistToBeAdded();
    Artist addedArtist = repository.save(artist);
    
    Assertions.assertThat(addedArtist.getId()).isNotNull();
    Assertions.assertThat(addedArtist.getName()).isNotNull();
    Assertions.assertThat(addedArtist.getName()).isEqualTo(artist.getName());
  }
  
  @Test
  @DisplayName("updateArtist")
  public void updateArtist() {
    Artist artist = ArtistCreator.createArtistToBeAdded();
    Artist addedArtist = repository.save(artist);
    addedArtist.setName("B-Updated");

    Artist updatedArtist = repository.save(addedArtist);
    
    Assertions.assertThat(addedArtist.getId()).isNotNull();
    Assertions.assertThat(addedArtist.getName()).isNotNull();
    Assertions.assertThat(addedArtist.getName()).isEqualTo(updatedArtist.getName());
  }
  
  @Test
  @DisplayName("deleteArtist")
  public void deleteArtist() {
    Artist artist = ArtistCreator.createArtistToBeAdded();
    Artist addedArtist = repository.save(artist);
    
    repository.delete(artist);
    
    Optional<Artist> artistOptional = repository.findById(addedArtist.getId());

    Assertions.assertThat(artistOptional.isEmpty()).isTrue();
  } 
 
  @Test
  @DisplayName("findByName")
  public void findByName() {
    Artist artist = ArtistCreator.createArtistToBeAdded();
    Artist addedArtist = repository.save(artist);
   
    String name = addedArtist.getName();
    
    List<Artist> list = repository.findByName(name);
    
    Assertions.assertThat(list).isNotEmpty();
    Assertions.assertThat(list).contains(addedArtist);
  }
  
  @Test
  @DisplayName("findByNameReturnEmptyList")
  public void findByNameReturnEmptyList() {
    String name = "fake-name";
    
    List<Artist> list = repository.findByName(name);
    
    Assertions.assertThat(list).isEmpty();
  }
  
  @Test
  @DisplayName("addThrowContraintViolationExceptionWhenNameIsEmpty")
  public void addThrowContraintViolationExceptionWhenNameIsEmpty() {
    Artist artist = new Artist();

    //Assertions.assertThatThrownBy(() -> repository.save(artist))
    //  .isInstanceOf(ConstraintViolationException.class);
    
    Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
      .isThrownBy(() -> repository.save(artist))
      .withMessageContaining("name cannot be empty");
  }
}
