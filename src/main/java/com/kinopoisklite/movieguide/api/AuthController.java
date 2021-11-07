package com.kinopoisklite.movieguide.api;

import com.kinopoisklite.movieguide.exception.PersistenceException;
import com.kinopoisklite.movieguide.model.Movie;
import com.kinopoisklite.movieguide.model.User;
import com.kinopoisklite.movieguide.security.jwt.JWTProvider;
import com.kinopoisklite.movieguide.service.MovieService;
import com.kinopoisklite.movieguide.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth")
public class AuthController {
    private final JWTProvider jwtProvider;
    private final UserService userService;
    private final MovieService movieService;

    @Resource(name = "passwordEncoder")
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<Map> getUserInfo(@RequestHeader(name = "Authorization") String auth) {
        if (!auth.startsWith("Bearer "))
            return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                    "Incorrect authorization header: 'Bearer <token>' expected!"));
        String token = auth.substring(7);
        if (jwtProvider.validateToken(token)) {
            String userId = jwtProvider.getUserId(auth.substring(7));
            User user = userService.findById(userId);
            List<Movie> favMovies = movieService.findByIds(user.getFavMovies());
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("favoriteMovies", favMovies);
            return ResponseEntity.ok().body(result);
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/me")
    public ResponseEntity<Map> updateUserInfo(@RequestHeader(name = "Authorization") String auth,
                                              @RequestBody Map<String, String> request) {
        if (!auth.startsWith("Bearer "))
            return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                    "Incorrect authorization header: 'Bearer <token>' expected!"));
        try {
            String token = auth.substring(7);
            if (jwtProvider.validateToken(token)) {
                String userId = jwtProvider.getUserId(auth.substring(7));
                User user = userService.findById(userId);
                userService.updateUser(userId, request.get("firstName"), request.get("lastName"));
                return ResponseEntity.ok().body(Collections.singletonMap("user", user));
            } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (PersistenceException pe) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", pe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Map> getToken(@RequestHeader(name = "Authorization") String auth) {
        if (!auth.startsWith("Basic "))
            return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                    "Incorrect authorization header: 'Basic <encoded_credentials>' expected!"));
        String[] credentials = new String(Base64.getDecoder().decode(auth.substring(6)))
                .split("\\:");
        try {
            String login = credentials[0];
            User user = userService.findByLogin(login);
            if (user == null)
                return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                        String.format("User %s not found!", login)));
            if (!user.getExternal()) {
                if (credentials.length < 2) {
                    return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                            "Incorrect basic authorization format: 'login:password' expected!"));
                }
                String password = credentials[1];
                if (!passwordEncoder.matches(password, user.getPassword()))
                    return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                            String.format("Incorrect password for %s!", login)));
            }
            Map<String, String> tokenResponse = jwtProvider.generateToken(user.getId());
            userService.setRefreshToken(user, tokenResponse.get("refreshToken"));
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<Map> refreshToken(@RequestBody Map<String, String> request) {
        if (!request.containsKey("token"))
            return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                    "Mandatory parameter 'token' is not present!"));
        if (!request.containsKey("refreshToken"))
            return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                    "Mandatory parameter 'refreshToken' is not present!"));
        String token = request.get("token");
        String refresh = request.get("refreshToken");
        if (token.isEmpty())
            return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                    "Mandatory parameter 'token' must not be empty!"));
        if (refresh.isEmpty())
            return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                    "Mandatory parameter 'refreshToken' must not be empty!"));
        try {
            String userId = jwtProvider.getUserId(token);
            User user = userService.findById(userId);
            if (user == null)
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "User not found"));
            if (user.getRefreshToken() == null)
                return ResponseEntity.badRequest().body(Collections.singletonMap("message",
                        String.format("No refresh token was given to user %s!", user.getLogin())));
            if (!user.getRefreshToken().equals(refresh))
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Invalid refresh token!"));
            Map<String, String> tokenResponse = jwtProvider.generateToken(user.getId());
            userService.setRefreshToken(user, tokenResponse.get("refreshToken"));
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

}
