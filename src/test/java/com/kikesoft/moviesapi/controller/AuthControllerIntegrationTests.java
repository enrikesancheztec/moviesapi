package com.kikesoft.moviesapi.controller;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kikesoft.moviesapi.entity.UserEntity;
import com.kikesoft.moviesapi.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private org.springframework.core.env.Environment env;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        UserEntity user = new UserEntity(null, "alice", passwordEncoder.encode("secret1"));
        user.setNew(true);
        userRepository.save(user);
    }

    @Test
    void login_withValidCredentials_returnsJwtString() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "password": "secret1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.matchesPattern("^[^.]+\\.[^.]+\\.[^.]+$")));
    }

    @Test
    void login_withInvalidCredentials_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void accessProtectedEndpoint_withExpiredToken_returnsUnauthorized() throws Exception {
        String expiredToken = generateExpiredToken();
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    private String generateExpiredToken() {
        String jwtKey = env.getProperty("jwt.tools.key");
        Key signingKey = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiration = new Date(now.getTime() - 1000); // Expired 1 second ago

        return Jwts.builder()
                .subject("alice")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }
}
