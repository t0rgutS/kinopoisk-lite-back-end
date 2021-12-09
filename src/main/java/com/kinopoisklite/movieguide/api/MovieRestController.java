package com.kinopoisklite.movieguide.api;

import com.kinopoisklite.movieguide.exception.PersistenceException;
import com.kinopoisklite.movieguide.model.Movie;
import com.kinopoisklite.movieguide.model.dto.MovieDTO;
import com.kinopoisklite.movieguide.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/movies")
public class MovieRestController {
    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        try {
            return ResponseEntity.ok().body(movieService.findAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> get(@PathVariable String id) {
        try {
            return ResponseEntity.ok().body(movieService.findById(id));
        } catch (PersistenceException pe) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", pe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    private MovieDTO parseRequest(Map request) {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle((String) request.get("title"));
        movieDTO.setReleaseYear((Integer) request.get("releaseYear"));
        movieDTO.setDuration((Integer) request.get("duration"));
        movieDTO.setDescription((String) request.get("description"));
        movieDTO.setGenre((String) request.get("genre"));
        movieDTO.setCountry((String) request.get("country"));
        movieDTO.setRatingCategory((String) request.get("ratingCategory"));
        if (request.containsKey("cover")) {
            MovieDTO.Cover cover = new MovieDTO.Cover();
            Map coverRequest = (Map) request.get("cover");
            cover.setFileName((String) coverRequest.get("fileName"));
            cover.setContent((String) coverRequest.get("content"));
            movieDTO.setCover(cover);
        }
        return movieDTO;
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Object> createMovie(@RequestBody Map request) {
        try {
            return ResponseEntity.ok().body(movieService.upsert(null, parseRequest(request)));
        } catch (PersistenceException pe) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", pe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}")
    @Secured({"ROLE_MODER", "ROLE_ADMIN"})
    public ResponseEntity<Object> updateMovie(@PathVariable String id, @RequestBody Map<String, Object> request) {
        try {
            return ResponseEntity.ok().body(movieService.upsert(id, parseRequest(request)));
        } catch (PersistenceException pe) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", pe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Map> deleteMovie(@PathVariable String id) {
        try {
            movieService.delete(id);
        } catch (PersistenceException pe) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", pe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
        return ResponseEntity.noContent().build();
    }
}
