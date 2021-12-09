package com.kinopoisklite.movieguide.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JWTProvider {
    @Value("$(jwt.secret)")
    private String key;

    public Map<String, String> generateToken(String userId) {
        Map<String, String> result = new HashMap<>();
        Date date = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        result.put("tokenType", "Bearer");
        result.put("token", Jwts.builder().setSubject(userId).setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, key).compact());
        result.put("expiresAt", "86400");
        result.put("refreshToken", UUID.randomUUID().toString().replaceAll("\\-", ""));
        return result;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public String getUserId(String token) {
        String userId;
        try {
            userId = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException e) {
            userId = e.getClaims().getSubject();
        }
        return userId;
    }
}
