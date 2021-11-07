package com.kinopoisklite.movieguide.security.oauth;

import com.kinopoisklite.movieguide.security.oauth.google.GoogleUserInfoProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInfoProviderPool {
    private final BeanFactory beanFactory;

    public UserInfoProvider getProvider(OAuthProviders provider) {
        switch (provider) {
            case GOOGLE:
                return (GoogleUserInfoProvider) beanFactory.getBean("googleUserInfoProvider");
        }
        return null;
    }
}
