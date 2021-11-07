package com.kinopoisklite.movieguide.security.oauth;

import java.util.Map;

public interface UserInfoProvider {
    Map<String, String> getUserInfo(String token);
}
