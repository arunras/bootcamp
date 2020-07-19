package com.arunx.boxapi.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.respository.ArtistRepository;
import com.arunx.boxapi.util.Utils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArtistService {
	
	private final Utils utils;
	private final ArtistRepository artistRepository;
	
	//@Transactional(rollbackFor = Exception.class)
	@Transactional
	public Artist add(Artist artist) {
		return artistRepository.save(artist);
	}
	
	public Artist update(Artist artist) {
		return artistRepository.save(artist);
	}

	public void delete(int id) {
		artistRepository.delete(utils.findArtistOrThrowNotFound(id, artistRepository));
	}
	
	public Artist findById(int id) {
		return utils.findArtistOrThrowNotFound(id, artistRepository);
	}
	
	public List<Artist> findByName(String name) {
		return artistRepository.findByName(name);
	}

	public List<Artist> listAll() {
		return artistRepository.findAll();
	}

	public Page<Artist> listAllPage(Pageable pageable) {
	  return artistRepository.findAll(pageable);
	}
	
}
