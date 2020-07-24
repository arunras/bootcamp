package com.arunx.boxapi.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.exception.ResourceNotFoundException;
import com.arunx.boxapi.respository.ArtistRepository;

@Component
public class Utils {
	
	public String formatLocalDateTimeToDatabaseDateTime(LocalDateTime localDateTime) {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
	}
	
	public Artist findArtistOrThrowNotFound(int id, ArtistRepository artistRepository) {
		return artistRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
	}
}
