package com.arunx.boxapi.security;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {
  
  private final UserRepository repository;

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return repository.findByUsername(username)
        .cast(UserDetails.class);
  }

}
