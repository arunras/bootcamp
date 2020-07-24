package com.arunx.boxapi.util;

import com.arunx.boxapi.domain.Artist;

public class ArtistCreator {

  public static Artist createArtistToBeSaved() {
    return Artist.builder()
            .name("Arun")
            .build();
  }
  
  public static Artist createValidArtist() {
    return Artist.builder()
            .id(1)
            .name("Arun")
            .build();
  }
  
  public static Artist createUpdatedArtist() {
    return Artist.builder()
            .id(1)
            .name("Arun R")
            .build();
  }

}
