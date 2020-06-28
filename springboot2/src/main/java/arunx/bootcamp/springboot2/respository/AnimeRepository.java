package arunx.bootcamp.springboot2.respository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import arunx.bootcamp.springboot2.domain.Anime;
import arunx.bootcamp.springboot2.util.Utils;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AnimeRepository {
	
	private final Utils utils;
	private static List<Anime> animes;
	
	static {
		animes = new ArrayList<>(List.of(
				new Anime(1, "Avata"),
				new Anime(2, "Bob"),
				new Anime(3, "Caven")
				));
	}

	public List<Anime> listAll() {
		return animes;
	}
	
	public Anime findById(int id) {
		return utils.findAnime(id, animes);
	}
	
	public Anime save(Anime anime) {
		anime.setId(ThreadLocalRandom.current().nextInt(4, 1000));
		animes.add(anime);
		return anime;
	}
	
	public void delete(int id) {
		animes.remove(animes.stream()
				.filter(anime -> anime.getId() == id)
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found")));
	}
	
	public void update(Anime anime) {
		animes.remove(findById(anime.getId()));
		animes.add(anime);
	}
}
