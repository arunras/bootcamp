package com.arunx.boxapi.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.wrapper.PageableResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BoxClient {
  private static String url = "http://localhost:8000/artists";

  public static void main(String[] args) {
    //testGetWithRestTemplate();
    //deleteTestTemplate();
  }
  
  private static HttpHeaders createJsonHeader() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    return httpHeaders;
  }
  
  private static void postRestTemplate() {
    Artist artist = Artist.builder().name("Arun").build();
    Artist arunSave = new RestTemplate().postForObject(url + "/add", artist, Artist.class);
    log.info("Arun saved id: {}", arunSave.getId());

    Artist artistB = Artist.builder().name("Bunthet").build();
    Artist bunthetSaved = new RestTemplate() 
        .exchange(url + "/add", HttpMethod.POST, new HttpEntity<>(artistB, createJsonHeader()), Artist.class).getBody();
    log.info("Bunthet saved id: {}", bunthetSaved.getId());
  }
  
  private static void putTestTemplate() {
    Artist artist = new Artist();
    artist.setId(12);
    artist.setName("Arun Rasmey");
    
    ResponseEntity<Artist> response = new RestTemplate() 
        .exchange(url + "/update", HttpMethod.PUT, new HttpEntity<>(artist, createJsonHeader()), Artist.class);
    log.info("Artist Updated: {}", response);
  }
  
  private static void deleteTestTemplate() {
    ResponseEntity<Void> response = new RestTemplate() 
        .exchange(url + "/delete/{id}", HttpMethod.DELETE, null, Void.class, 17);
    log.info("Artist Deleted: {}", response);
  }

  private static void getRestTemplate() {
    // Entity
    ResponseEntity<Artist> artistResponseEntity = new RestTemplate()
            .getForEntity(url + "/{id}", Artist.class, 2);
    
    log.info("Response Entity {}", artistResponseEntity);
    log.info("Response Data {}", artistResponseEntity.getBody());
   
    // Object
    Artist artist = new RestTemplate()
        .getForObject(url + "/{id}", Artist.class, 2);
    
    log.info("Artist {}", artist);
    
    // Array
    Artist[] artists = new RestTemplate()
        .getForObject(url + "/list", Artist[].class);
    
    log.info("Artist Array {}", Arrays.toString(artists));
    
    // Exchage
    ResponseEntity<List<Artist>> exchange = new RestTemplate()
        .exchange(url + "/list", HttpMethod.GET, null, new ParameterizedTypeReference<List<Artist>>() {});
    
    log.info("Artist List {}", exchange.getBody());
   
    // Pageable
    ResponseEntity<PageableResponse<Artist>> exchangePage = new RestTemplate()
        .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<PageableResponse<Artist>>() {});
    
    log.info("Artist List {}", exchangePage.getBody());
  }

}
