package ru.mirea.movieguide.api;

import ru.mirea.movieguide.exception.NotFoundException;
import ru.mirea.movieguide.service.AgeRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/AgeRatings")
public class AgeRatingRestController {
    private final AgeRatingService ageRatingService;

    @GetMapping
    public ResponseEntity<Map> getAll() {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("AgeRatings", ageRatingService.getAll());
        } catch (Exception e) {
            result.put("error", e.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map> get(String id) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("AgeRatings", Collections.singletonList(ageRatingService.get(id)));
        } catch (NotFoundException nfe) {
            result.put("error", nfe.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }
}
