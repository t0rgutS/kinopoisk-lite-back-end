package ru.mirea.movieguide.api;

import ru.mirea.movieguide.exception.NotFoundException;
import ru.mirea.movieguide.exception.PersistenceException;
import ru.mirea.movieguide.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/Movies")
public class MovieRestController {
    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<Map> getAll() {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("content", movieService.getAll());
            result.put("status", "OK");
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map> get(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("content", movieService.get(id));
            result.put("status", "OK");
        } catch (NotFoundException nfe) {
            result.put("status", "ERROR");
            result.put("error", nfe.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

    @PostMapping
    @PutMapping
    @Secured("ADMIN")
    public ResponseEntity<Map> upsertMovie(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("entity", movieService.upsert(request));
            result.put("status", "OK");
        } catch (PersistenceException pe) {
            result.put("status", "ERROR");
            result.put("error", pe.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }
}
