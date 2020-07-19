package com.arunx.boxapi.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    /***
     * BasicAuthenticationFilter
     * UsernamePasswordAuthenticationFilter
     * DefaultLoginPageGeneratingFilter
     * DefaultLogoutPageGeneratingFilter
     * FilterSecurityInterceptor
     * Authentication -> Authorization
     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.csrf()
          .disable()
          //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
          //.and()
          .authorizeRequests()
          .antMatchers("/artists/admin/**").hasRole("ADMIN")
          .antMatchers("/artists/**").hasRole("USER")
          .antMatchers("/actuator/**").permitAll()
          .anyRequest().authenticated()
          .and()
          .formLogin()
          .and()
          .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
      log.info("TEST PASSWORD {}", passwordEncoder.encode("test"));

      auth.inMemoryAuthentication()
          .withUser("user2")
          .password(passwordEncoder.encode("user"))
          .roles("USER")
          .and()
          .withUser("admin2")
          .password(passwordEncoder.encode("admin"))
          .roles("USER", "ADMIN");

      auth.userDetailsService(userService)
          .passwordEncoder(passwordEncoder);
    }
}
