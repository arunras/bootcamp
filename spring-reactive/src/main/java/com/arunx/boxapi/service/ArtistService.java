package com.arunx.boxapi.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.repository.ArtistRepository;

import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArtistService {
  
  private final ArtistRepository repository;
  
  public Mono<Artist> add(Artist artist) {
    return repository.save(artist);
  }
  
  @Transactional
  public Flux<Artist> addAll(List<Artist> artists) {
    return repository.saveAll(artists)
                     .doOnNext(this::throwResponseStatusExceptionWhenEmptyName);
  }
  
  private void throwResponseStatusExceptionWhenEmptyName(Artist artist) {
    if (StringUtil.isNullOrEmpty(artist.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid name");
    }
  }
  
  public Mono<Artist> update(Artist artist) {
    return findById(artist.getId())
             .map(artistFound -> artist.withId(artistFound.getId()))
             .flatMap(repository::save);
  }
  
  public Mono<Void> delete(int id) {
    return findById(id)
            .flatMap(repository::delete);
  }
  
  public Flux<Artist> findAll() {
    return repository.findAll();
  }
  
  public Mono<Artist> findById(int id) {
    return repository.findById(id)
        .switchIfEmpty(this.monoReponseStatusNotFoundException());
  }
  
  public <T> Mono<T> monoReponseStatusNotFoundException() {
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found"));
  }

}
