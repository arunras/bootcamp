package arunx.bootcamp.springboot2.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import arunx.bootcamp.springboot2.domain.Anime;

@Component
public class Utils {
	
	public String formatLocalDateTimeToDatabaseDateTime(LocalDateTime localDateTime) {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
	}
	
	public Anime findAnime(int id, List<Anime> animes) {
		return animes.stream()
				.filter(anime -> anime.getId() == id)
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));
	}
}
