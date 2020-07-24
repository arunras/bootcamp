package com.arunx.boxapi.util;

import org.springframework.context.ApplicationContext;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebTestClientUtilMe {
  private final ApplicationContext appContext;
  
  public WebTestClient authenticateClient(String username, String password) {
    return WebTestClient.bindToApplicationContext(appContext)
        .apply(SecurityMockServerConfigurers.springSecurity())
        .configureClient()
        .filter(ExchangeFilterFunctions.basicAuthentication(username, password))
        .build();
  }

}
