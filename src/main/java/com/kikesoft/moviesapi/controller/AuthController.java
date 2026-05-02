package com.kikesoft.moviesapi.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kikesoft.moviesapi.service.JwtTools;
import com.kikesoft.moviesapi.vo.CredentialsVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller that exposes authentication operations.
 *
 * @author Enrique Sanchez
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Operations for JWT token generation")
class AuthController {
    private static final Logger LOGGER = LogManager.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTools jwtTools;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Authenticates credentials and returns a JWT string.
     *
     * @param credentials login credentials payload
     * @return JWT token as plain text when credentials are valid
     */
    @PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Login and generate JWT", description = "Authenticates username/password and returns JWT as plain text")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, token generated", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "eyJhbGciOiJIUzI1NiJ9..."))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Invalid credentials")))
    })
    ResponseEntity<String> login(@Valid @RequestBody final CredentialsVO credentials) {
        LOGGER.debug("POST /auth/login - authenticating username='{}'", credentials.getUsername());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                credentials.getUsername(),
                credentials.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(credentials.getUsername());
        String token = jwtTools.generateToken(userDetails.getUsername());
        LOGGER.debug("POST /auth/login - token generated successfully for username='{}'", credentials.getUsername());
        return ResponseEntity.ok(token);
    }
}
