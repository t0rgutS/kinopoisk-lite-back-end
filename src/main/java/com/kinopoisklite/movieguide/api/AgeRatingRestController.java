package com.kinopoisklite.movieguide.api;

import com.kinopoisklite.movieguide.exception.PersistenceException;
import com.kinopoisklite.movieguide.service.AgeRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/ageRatings")
public class AgeRatingRestController {
    private final AgeRatingService ageRatingService;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        try {
            return ResponseEntity.ok().body( ageRatingService.getAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> get(String id) {
        try {
            return ResponseEntity.ok().body(ageRatingService.get(id));
        } catch (PersistenceException pe) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", pe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}
