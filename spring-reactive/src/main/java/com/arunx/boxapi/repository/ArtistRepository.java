package com.arunx.boxapi.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.arunx.boxapi.domain.Artist;

import reactor.core.publisher.Mono;

@Repository
public interface ArtistRepository extends ReactiveCrudRepository<Artist, Integer>{
  
  Mono<Artist> findById(int id);

}
