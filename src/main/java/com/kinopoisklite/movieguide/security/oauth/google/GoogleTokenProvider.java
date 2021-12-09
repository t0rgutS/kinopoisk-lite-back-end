package com.kinopoisklite.movieguide.security.oauth.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinopoisklite.movieguide.security.oauth.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GoogleTokenProvider implements TokenProvider {
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    @Value("${oauth.google.token-url}")
    private String tokenServiceURL;

    @Value("${oauth.google.id}")
    private String clientId;

    @Value("${oauth.google.secret}")
    private String clientSecret;

    @Value("${oauth.google.redirect}")
    private String redirectURI;

    @Override
    public Map<String, String> getToken(String authCode) {
        String url = String.format("%s?client_id=%s" +
                "&client_secret=%s" +
                "&redirect_uri=%s" +
                "&grant_type=authorization_code" +
                "&code=%s", tokenServiceURL, clientId, clientSecret, redirectURI, authCode);
        try {
            HttpEntity response = restTemplate.exchange(url, HttpMethod.POST, null, Map.class);
            return mapper.convertValue(response.getBody(), Map.class);
        } catch (HttpStatusCodeException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
