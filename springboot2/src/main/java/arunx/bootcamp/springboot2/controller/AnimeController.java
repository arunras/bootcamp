package arunx.bootcamp.springboot2.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import arunx.bootcamp.springboot2.domain.Anime;
import arunx.bootcamp.springboot2.respository.AnimeRepository;
import arunx.bootcamp.springboot2.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/animes")
@Slf4j
@RequiredArgsConstructor
public class AnimeController {
	
	private final Utils dateUtil;
	private final AnimeRepository animeRepository;

	@GetMapping
	public ResponseEntity<List<Anime>> listAll() {
		log.info("Date Formatted {}", dateUtil.formatLocalDateTimeToDatabaseDateTime(LocalDateTime.now()));
		return new ResponseEntity<>(animeRepository.listAll(), HttpStatus.OK);
	}
	
	@GetMapping(path = "{id}")
	public ResponseEntity<Anime> findById(@PathVariable int id) {
		return ResponseEntity.ok(animeRepository.findById(id));
	}
	
	@PostMapping(path = "/add")
	public ResponseEntity<Anime> save(@RequestBody Anime anime) {
		return ResponseEntity.ok(animeRepository.save(anime));
	}
	
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable int id) {
		animeRepository.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping(path = "/update")
	public ResponseEntity<Void> update(@RequestBody Anime anime) {
		animeRepository.update(anime);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
}
