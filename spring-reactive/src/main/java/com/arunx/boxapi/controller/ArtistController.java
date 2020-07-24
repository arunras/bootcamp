package com.arunx.boxapi.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.service.ArtistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("artists")
@Slf4j
@RequiredArgsConstructor
@SecurityScheme(
    name = "Basic Authentication",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)
public class ArtistController {
  
  private final ArtistService service;
  
  @PostMapping(path = "add")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(security = @SecurityRequirement(name = "Basic Authentication"),
             tags = {"anime"})
  public Mono<Artist> add(@RequestBody @Valid Artist artist) {
    return service.add(artist);
  }
  
  @PostMapping(path = "addBatch")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(security = @SecurityRequirement(name = "Basic Authentication"),
             tags = {"anime"})
  public Flux<Artist> addBatch(@RequestBody List<Artist> artists) {
    return service.addAll(artists);
  }
  
  @PutMapping(path = "update")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(security = @SecurityRequirement(name = "Basic Authentication"),
             tags = {"anime"})
  public Mono<Artist> update(@RequestBody @Valid Artist artist) {
    return service.update(artist);
  }
  
  @DeleteMapping(path = "delete/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(security = @SecurityRequirement(name = "Basic Authentication"),
             tags = {"anime"})
  public Mono<Void> delete(@PathVariable int id) {
    return service.delete(id);
  }
  
  @GetMapping(path = "list")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "List all artists",
    security = @SecurityRequirement(name = "Basic Authentication"),
    tags = {"anime"})
  public Flux<Artist> listAll() {
    return service.findAll();
  }
  
  @GetMapping(path = "find/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(security = @SecurityRequirement(name = "Basic Authentication"),
             tags = {"anime"})
  public Mono<Artist> findById(@PathVariable int id) {
    return service.findById(id);
  }

}
