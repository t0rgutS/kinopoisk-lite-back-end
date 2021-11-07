package com.kinopoisklite.movieguide.api;

import com.kinopoisklite.movieguide.exception.PersistenceException;
import com.kinopoisklite.movieguide.model.User;
import com.kinopoisklite.movieguide.security.jwt.JWTProvider;
import com.kinopoisklite.movieguide.security.oauth.*;
import com.kinopoisklite.movieguide.security.oauth.google.GoogleTokenProvider;
import com.kinopoisklite.movieguide.security.oauth.google.GoogleUserInfoProvider;
import com.kinopoisklite.movieguide.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final JWTProvider jwtProvider;
    private final UserService userService;

    @Autowired
    private TokenProviderPool tokenProviderPool;

    @Autowired
    private UserInfoProviderPool userInfoProviderPool;


    private ResponseEntity<Map<String, String>> authorizeExternal(String code, TokenProvider tokenProvider,
                                                                  UserInfoProvider userInfoProvider) {
        if (code == null)
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Authorization code is null!"));
        if (code.isEmpty())
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Authorization code is empty!"));
        Map<String, String> token = tokenProvider.getToken(code);
        Map<String, String> userInfo = userInfoProvider.getUserInfo(token.get("access_token"));
        try {
            User user = userService.findById(userInfo.get("id"));
            if (user == null)
                user = userService.createUser(userInfo.get("id"), userInfo.get("id"), "1234",
                        userInfo.get("given_name"), userInfo.get("family_name"), true, null);
            Map<String, String> tokenResponse = jwtProvider.generateToken(user.getId());
            userService.setRefreshToken(user, tokenResponse.get("refreshToken"));
            return ResponseEntity.ok(tokenResponse);
        } catch (PersistenceException pe) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", pe.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping("/google")
    public ResponseEntity<Map<String, String>> authWGoogle(@RequestParam String code) {
        GoogleTokenProvider tokenProvider = (GoogleTokenProvider) tokenProviderPool
                .getProvider(OAuthProviders.GOOGLE);
        GoogleUserInfoProvider userInfoProvider = (GoogleUserInfoProvider) userInfoProviderPool
                .getProvider(OAuthProviders.GOOGLE);
        return authorizeExternal(code, tokenProvider, userInfoProvider);
    }
}
