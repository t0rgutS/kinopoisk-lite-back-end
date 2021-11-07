package com.kinopoisklite.movieguide.security.oauth;

import java.util.Map;

public interface TokenProvider {
    Map<String, String> getToken(String authCode);
}
