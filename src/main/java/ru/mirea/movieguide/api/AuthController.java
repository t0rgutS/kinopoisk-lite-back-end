package ru.mirea.movieguide.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mirea.movieguide.exception.PersistenceException;
import ru.mirea.movieguide.model.User;
import ru.mirea.movieguide.security.JWTProvider;
import ru.mirea.movieguide.service.UserService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth")
public class AuthController {
    private final JWTProvider jwtProvider;
    private final UserService userService;

    @Resource(name = "passwordEncoder")
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<Map> getToken(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            if(!request.containsKey("username") || !request.containsKey("password")) {
                result.put("error", "Обязательные параметры username и password отсутствуют!");
                return new ResponseEntity<Map>(result, HttpStatus.BAD_REQUEST);
            }
            User user = userService.findUserByUsername(request.get("username"));
            if(!passwordEncoder.matches(request.get("password"), user.getPassword())) {
                result.put("error", "Неверный пароль!");
                return new ResponseEntity<Map>(result, HttpStatus.BAD_REQUEST);
            }
            String token = jwtProvider.generateToken(user.getId());
            String refreshToken = userService.setRefreshToken(user, false);
            result.put("tokenType", "Bearer");
            result.put("token", token);
            result.put("expiresAt", "86400");
            result.put("refreshToken", refreshToken);
        } catch (PersistenceException e) {
            e.printStackTrace();
            result.put("error", e.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", e.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<Map> refreshToken(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            if(!request.containsKey("refreshToken")) {
                result.put("error", "Укажите refresh token!");
                return new ResponseEntity<Map>(result, HttpStatus.BAD_REQUEST);
            }
            User user = userService.findTokenOwner(request.get("refreshToken"));
            if (user.getRefreshToken() == null) {
                result.put("error", "No refresh token!");
                return new ResponseEntity<Map>(result, HttpStatus.BAD_REQUEST);
            }
            String token = jwtProvider.generateToken(user.getId());
            String newRefreshToken = userService.setRefreshToken(user, true);
            result.put("tokenType", "Bearer");
            result.put("token", token);
            result.put("expiresAt", "86400");
            result.put("refreshToken", newRefreshToken);
        } catch (PersistenceException e) {
            e.printStackTrace();
            result.put("error", e.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", e.getMessage());
            return new ResponseEntity<Map>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

}
