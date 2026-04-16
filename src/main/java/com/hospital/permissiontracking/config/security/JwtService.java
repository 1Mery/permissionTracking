package com.hospital.permissiontracking.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    //token sahtemi
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final long jwtExpiration = 1000 * 60 * 60; // 1 saat

    //token üretme
    public String generateToken(String email, String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey)
                .compact();
    }

    //token dan  email çekme
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    //tokenden role çekme
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    //valid mi
    public boolean isTokenValid(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email)) && !isTokenExpired(token);
    }

    //expire mı
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
