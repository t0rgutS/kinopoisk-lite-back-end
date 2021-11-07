package com.kinopoisklite.movieguide.security.oauth;

import com.kinopoisklite.movieguide.security.oauth.google.GoogleTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenProviderPool {
    private final BeanFactory beanFactory;

    public TokenProvider getProvider(OAuthProviders provider) {
        switch (provider) {
            case GOOGLE:
                return (GoogleTokenProvider) beanFactory.getBean("googleTokenProvider");
        }
        return null;
    }
}
