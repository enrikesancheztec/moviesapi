package com.kikesoft.moviesapi.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Unit tests for JwtTools.
 *
 * @author Enrique Sanchez
 */
@SpringBootTest
@ActiveProfiles("test")
class JwtToolsTests {

    @Autowired
    private JwtTools jwtTools;

    @Autowired
    private org.springframework.core.env.Environment env;

    private String validToken;

    @BeforeEach
    void setUp() {
        validToken = jwtTools.generateToken("testuser");
    }

    @Test
    void generateToken_returnsValidJwtString() {
        String token = jwtTools.generateToken("alice");

        assertNotNull(token);
        assertTrue(token.contains("."), "Token should contain JWT structure with dots");
        assertEquals(3, token.split("\\.").length, "Token should have 3 parts (header.payload.signature)");
    }

    @Test
    void extractUsername_withValidToken_returnsUsername() {
        String token = jwtTools.generateToken("alice");
        String username = jwtTools.extractUsername(token);

        assertEquals("alice", username);
    }

    @Test
    void isTokenValid_withValidToken_returnsTrue() {
        assertTrue(jwtTools.isTokenValid(validToken));
    }

    @Test
    void isTokenValid_withExpiredToken_returnsFalse() {
        String expiredToken = generateExpiredToken("alice");
        assertFalse(jwtTools.isTokenValid(expiredToken));
    }

    @Test
    void isTokenValid_withMalformedToken_returnsFalse() {
        String malformedToken = "not.a.token";
        assertFalse(jwtTools.isTokenValid(malformedToken));
    }

    @Test
    void isTokenValid_withTamperedToken_returnsFalse() {
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";
        assertFalse(jwtTools.isTokenValid(tamperedToken));
    }

    @Test
    void isTokenValid_withWrongSignatureKey_returnsFalse() {
        String wrongKeyToken = generateTokenWithWrongKey("alice");
        assertFalse(jwtTools.isTokenValid(wrongKeyToken));
    }

    private String generateExpiredToken(String username) {
        String jwtKey = env.getProperty("jwt.tools.key");
        Key signingKey = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiration = new Date(now.getTime() - 1000); // Expired 1 second ago

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    private String generateTokenWithWrongKey(String username) {
        // Use a different secret key than the configured one
        String wrongKey = "this-is-a-wrong-secret-key-that-is-long-enough";
        Key signingKey = Keys.hmacShaKeyFor(wrongKey.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }
}
