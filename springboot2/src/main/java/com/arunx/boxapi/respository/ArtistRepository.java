package com.arunx.boxapi.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arunx.boxapi.domain.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
	
	List<Artist> findByName(String name);
}
