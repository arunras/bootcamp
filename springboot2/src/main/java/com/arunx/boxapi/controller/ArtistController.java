package com.arunx.boxapi.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arunx.boxapi.domain.Artist;
import com.arunx.boxapi.service.ArtistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/artists")
@Slf4j
@RequiredArgsConstructor
public class ArtistController {
	
	private final ArtistService artistService;
	
	@PostMapping(path = "/add")
	public ResponseEntity<Artist> add(@RequestBody @Valid Artist artist) {
	  return ResponseEntity.ok(artistService.add(artist));
	}

	@PutMapping(path = "/update")
	public ResponseEntity<Artist> update(@RequestBody Artist artist) {
	  return ResponseEntity.ok(artistService.update(artist));
	}
	
	@DeleteMapping(path = "/admin/delete/{id}")
	//@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable int id) {
      artistService.delete(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(path = "/find/{id}")
	//@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Artist> findById(@PathVariable int id, @AuthenticationPrincipal UserDetails userDetails) {
	  log.info("USER DETAILS: {}", userDetails);
	  
	  return ResponseEntity.ok(artistService.findById(id));
	}
	
	@GetMapping(path = "/find")
	public ResponseEntity<List<Artist>> findByName(@RequestParam(value = "name") String name) {
		return ResponseEntity.ok(artistService.findByName(name));
	}

	@GetMapping(path = "/list")
	@Operation(summary = "List all artist", description = "All artist will be query", tags = {"artist"})
	public ResponseEntity<List<Artist>> listAll() {
		return new ResponseEntity<>(artistService.listAll(), HttpStatus.OK);
	}

	@GetMapping(path = "/pages")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Successfull operation"),
	    @ApiResponse(responseCode = "404", description = "There is no artists exist")
	})
	public ResponseEntity<Page<Artist>> listAllPage(Pageable pageable) {
		return new ResponseEntity<>(artistService.listAllPage(pageable), HttpStatus.OK);
	}
	
}
