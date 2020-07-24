package com.arunx.boxapi.exception;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CustomAttributes extends DefaultErrorAttributes {
  
  @Override
  public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
    Map<String, Object> errorAttributesMap = super.getErrorAttributes(request, options);
    Throwable throwable = getError(request);
    
    if (throwable instanceof ResponseStatusException) {
      ResponseStatusException e = (ResponseStatusException) throwable;
      errorAttributesMap.put("message", e.getMessage());
      errorAttributesMap.put("developerMessage", "A ResponseStatusException Happened");
      return errorAttributesMap;
    }
    
    return errorAttributesMap;

  }
}
