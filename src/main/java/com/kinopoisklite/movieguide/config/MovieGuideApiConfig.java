package com.kinopoisklite.movieguide.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinopoisklite.movieguide.security.oauth.TokenProvider;
import com.kinopoisklite.movieguide.security.oauth.TokenProviderPool;
import com.kinopoisklite.movieguide.security.oauth.UserInfoProvider;
import com.kinopoisklite.movieguide.security.oauth.UserInfoProviderPool;
import com.kinopoisklite.movieguide.security.oauth.google.GoogleTokenProvider;
import com.kinopoisklite.movieguide.security.oauth.google.GoogleUserInfoProvider;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MovieGuideApiConfig {
    @Autowired
    private BeanFactory beanFactory;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TokenProvider googleTokenProvider(ObjectMapper objectMapper, RestTemplate restTemplate) {
        return new GoogleTokenProvider(objectMapper, restTemplate);
    }

    @Bean
    public UserInfoProvider googleUserInfoProvider(ObjectMapper objectMapper, RestTemplate restTemplate) {
        return new GoogleUserInfoProvider(objectMapper, restTemplate);
    }

   /* @Bean
    public TokenProviderPool tokenProviderPool(BeanFactory beanFactory) {
        return new TokenProviderPool(beanFactory);
    }

    @Bean
    public UserInfoProviderPool userInfoProviderPool(BeanFactory beanFactory) {
        return new UserInfoProviderPool(beanFactory);
    }*/
}
