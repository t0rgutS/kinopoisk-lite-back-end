package com.kinopoisklite.movieguide.api;

import com.kinopoisklite.movieguide.model.Movie;
import com.kinopoisklite.movieguide.model.User;
import com.kinopoisklite.movieguide.security.jwt.JWTProvider;
import com.kinopoisklite.movieguide.service.MovieService;
import com.kinopoisklite.movieguide.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/favorite")
public class FavoriteMoviesRestController {
    private final JWTProvider jwtProvider;
    private final MovieService movieService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Object> getAllFavorites(@RequestHeader(name = "Authorization") String auth) {
      try {
        if (auth == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (auth.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!auth.startsWith("Bearer "))
            return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                    "Incorrect authorization header: 'Bearer <token>' expected!"));
        String token = auth.substring(7);
        if (jwtProvider.validateToken(token)) {
            String userId = jwtProvider.getUserId(auth.substring(7));
            User user = userService.findById(userId);
            List<Movie> favMovies = movieService.findByIds(user.getFavMovies());
            return ResponseEntity.ok().body(favMovies);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
    }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map> isFavorite(@RequestHeader(name = "Authorization") String auth,
                                          @PathVariable String id) {
        try {
            if (auth == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            if (auth.isEmpty())
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            if (!auth.startsWith("Bearer "))
                return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                        "Incorrect authorization header: 'Bearer <token>' expected!"));
            String token = auth.substring(7);
            if (jwtProvider.validateToken(token)) {
                String userId = jwtProvider.getUserId(auth.substring(7));
                User user = userService.findById(userId);
                return ResponseEntity.ok().body(Collections.singletonMap("favorite", user.getFavMovies().contains(id)));
            } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<Object> addToFav(@RequestHeader(name = "Authorization") String auth,
                                      @PathVariable String id) {
        try {
            if (auth == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            if (auth.isEmpty())
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            if (!auth.startsWith("Bearer "))
                return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                        "Incorrect authorization header: 'Bearer <token>' expected!"));
            String token = auth.substring(7);
            if (jwtProvider.validateToken(token)) {
                String userId = jwtProvider.getUserId(auth.substring(7));
                User user = userService.findById(userId);
                Movie movie = movieService.findById(id);
                userService.addToFav(user, id);
                return ResponseEntity.ok().body(movie);
            } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> removeFromFav(@RequestHeader(name = "Authorization") String auth,
                                           @PathVariable String id) {
        try {
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
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}
