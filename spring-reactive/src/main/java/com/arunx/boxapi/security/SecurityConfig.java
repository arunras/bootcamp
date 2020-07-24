package com.arunx.boxapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        //@formatter:off
        return http
            .csrf().disable()
            .authorizeExchange()
              .pathMatchers(HttpMethod.POST, "/artists/**").hasRole("ADMIN")
              .pathMatchers(HttpMethod.PUT, "/artists/**").hasRole("ADMIN")
              .pathMatchers(HttpMethod.DELETE, "/artists/**").hasRole("ADMIN")
              .pathMatchers(HttpMethod.GET, "/artists/**").hasRole("USER")
              .pathMatchers("/swagger-ui.html",
                  "/swagger-ui/**",
                  "/v3/api-docs/**",
                  "/webjars/**")
            .permitAll()
            .anyExchange().authenticated()
            .and()
                .formLogin()
            .and()
                .httpBasic()
            .and()
                .build();
        //@formatter:on
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(UserService userService) {
      return new UserDetailsRepositoryReactiveAuthenticationManager(userService);
    }
    
    
    
    /*
    public MapReactiveUserDetailsService userDetailsServiceMemory() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserDetails user = User.withUsername("user")
            .password(passwordEncoder.encode("test"))
            .roles("USER")
            .build();

        UserDetails admin = User.withUsername("admin")
            .password(passwordEncoder.encode("test"))
            .roles("USER", "ADMIN")
            .build();

        return new MapReactiveUserDetailsService(user, admin);
    }
    */
}
