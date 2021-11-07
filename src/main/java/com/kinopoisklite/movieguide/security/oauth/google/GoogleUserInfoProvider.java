package com.kinopoisklite.movieguide.security.oauth.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinopoisklite.movieguide.security.oauth.UserInfoProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleUserInfoProvider implements UserInfoProvider {
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    @Value("${oauth.google.user-info-url}")
    private String userInfoURL;

    @Override
    public Map<String, String> getUserInfo(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity entity = new HttpEntity(headers);
        try {
            HttpEntity response = restTemplate.exchange(userInfoURL, HttpMethod.GET, entity, Map.class);
            return mapper.convertValue(response.getBody(), Map.class);
        } catch (HttpStatusCodeException e) {

        }
        return new HashMap<>();
    }
}
