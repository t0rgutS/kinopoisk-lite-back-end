package ru.mirea.movieguide.api;

import org.springframework.security.access.prepost.PreAuthorize;
import ru.mirea.movieguide.exception.NotFoundException;
import ru.mirea.movieguide.exception.PersistenceException;
import ru.mirea.movieguide.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
            result.put("Movies", movieService.getAll());
        } catch (Exception e) {
            result.put("error", e.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map> get(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("Movies", Collections.singletonList(movieService.get(id)));
        } catch (NotFoundException nfe) {
            result.put("error", nfe.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.NOT_FOUND);
        } catch (PersistenceException pe) {
            result.put("error", pe.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> upsertMovie(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("Movies", Collections.singletonList(movieService.upsert(request)));
        } catch (NotFoundException nfe) {
            result.put("error", nfe.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.NOT_FOUND);
        } catch (PersistenceException pe) {
            result.put("error", pe.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> deleteMovie(@PathVariable String id) {
        try {
            movieService.delete(id);
        } catch (NotFoundException nfe) {
            return new ResponseEntity<Map>(Collections.singletonMap("error", nfe.getMessage()), HttpStatus.NOT_FOUND);
        } catch (PersistenceException pe) {
            return new ResponseEntity<Map>(Collections.singletonMap("error", pe.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<Map>(Collections.singletonMap("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map>(HttpStatus.NO_CONTENT);
    }
}
