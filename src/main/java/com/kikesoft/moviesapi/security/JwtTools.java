package com.kikesoft.moviesapi.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Minimal JWT utility for token generation.
 *
 * @author Enrique Sanchez
 */
@Service
public class JwtTools {

    /**
     * Signing key configured from environment properties.
     */
    @Value("${jwt.tools.key}")
    private String jwtToolsKey;

    /**
     * Generates a signed JWT for the provided username.
     *
     * @param username authenticated username
     * @return JWT token as compact string
     */
    public String generateToken(final String username) {
        Date now = new Date();
        // Add 3,600,000 ms (1 hour) to define the token expiration time.
        Date expiration = new Date(now.getTime() + 3600000);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Builds HMAC signing key from configured secret.
     *
     * @return signing key
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtToolsKey.getBytes(StandardCharsets.UTF_8));
    }
}
