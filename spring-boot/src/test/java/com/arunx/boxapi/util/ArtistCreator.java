package com.arunx.boxapi.util;

import com.arunx.boxapi.domain.Artist;

public class ArtistCreator {

  public static Artist createArtistToBeAdded() {
    Artist artist = new Artist();
    artist.setName("A");
    return artist;
  }
  
  public static Artist createValidArtist() {
    Artist artist = new Artist();
    artist.setName("A");
    artist.setId(1);
    return artist;
  }
  
  public static Artist createValidUpdatedArtist() {
    Artist artist = new Artist();
    artist.setName("B-Updated");
    artist.setId(1);
    return artist;
  }
  
}