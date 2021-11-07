package com.kinopoisklite.movieguide.api;

import com.kinopoisklite.movieguide.exception.PersistenceException;
import com.kinopoisklite.movieguide.model.User;
import com.kinopoisklite.movieguide.security.jwt.JWTProvider;
import com.kinopoisklite.movieguide.service.MovieService;
import com.kinopoisklite.movieguide.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/movies")
public class MovieRestController {
    private final JWTProvider jwtProvider;
    private final MovieService movieService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Map> getAll() {
        try {
            return ResponseEntity.ok().body(Collections.singletonMap("movies",
                    movieService.findAll()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map> get(@PathVariable String id) {
        try {
            return ResponseEntity.ok().body(Collections.singletonMap("movie",
                    movieService.findById(id)));
        } catch (PersistenceException pe) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", pe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping(value = "/{id}/favorite")
    public ResponseEntity<?> addToFav(@RequestHeader(name = "Authorization") String auth,
                                      @PathVariable String id) {
        if (!auth.startsWith("Bearer "))
            return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                    "Incorrect authorization header: 'Bearer <token>' expected!"));
        String token = auth.substring(7);
        if (jwtProvider.validateToken(token)) {
            String userId = jwtProvider.getUserId(auth.substring(7));
            User user = userService.findById(userId);
            userService.addToFav(user, id);
            return ResponseEntity.noContent().build();
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping(value = "/{id}/favorite")
    public ResponseEntity<?> removeFromFav(@RequestHeader(name = "Authorization") String auth,
                                           @PathVariable String id) {
        if (!auth.startsWith("Bearer "))
            return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                    "Incorrect authorization header: 'Bearer <token>' expected!"));
        String token = auth.substring(7);
        if (jwtProvider.validateToken(token)) {
            String userId = jwtProvider.getUserId(auth.substring(7));
            User user = userService.findById(userId);
            userService.removeFromFav(user, id);
            return ResponseEntity.noContent().build();
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Map> createMovie(@RequestBody Map<String, Object> request) {
        try {
            return ResponseEntity.ok().body(Collections.singletonMap("movie",
                    movieService.upsert(null, request)));
        } catch (PersistenceException pe) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", pe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT})
    @Secured({"ROLE_MODER", "ROLE_ADMIN"})
    public ResponseEntity<Map> updateMovie(@PathVariable String id, @RequestBody Map<String, Object> request) {
        try {
            return ResponseEntity.ok().body(Collections.singletonMap("movie",
                    movieService.upsert(id, request)));
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
