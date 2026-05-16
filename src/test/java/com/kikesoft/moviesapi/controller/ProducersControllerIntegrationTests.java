package com.kikesoft.moviesapi.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.kikesoft.moviesapi.entity.MovieEntity;
import com.kikesoft.moviesapi.entity.ProducerEntity;
import com.kikesoft.moviesapi.entity.Role;
import com.kikesoft.moviesapi.entity.UserEntity;
import com.kikesoft.moviesapi.enumeration.Rating;
import com.kikesoft.moviesapi.repository.MovieRepository;
import com.kikesoft.moviesapi.repository.ProducerRepository;
import com.kikesoft.moviesapi.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProducersControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private MovieRepository movieRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

    private Long producerId;
        private String bearerToken;
                private String adminBearerToken;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
        producerRepository.deleteAll();
                userRepository.deleteAll();
                createUser("alice", passwordEncoder.encode("secret1"), Role.USER);
                createUser("admin", passwordEncoder.encode("secret1"), Role.ADMIN);
                bearerToken = loginAndGetToken("alice", "secret1");
                adminBearerToken = loginAndGetToken("admin", "secret1");
        ProducerEntity producer = createProducer("John Smith", "Award-winning producer.");
        producerId = producer.getId();
        createMovieForProducer(producer);
        }

        @Test
        void getProducerById_withoutToken_returnsUnauthorized() throws Exception {
                mockMvc.perform(get("/producers/{id}", producerId))
                                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
    }

        @Test
	void getProducerById_withAdminRole_succeeds() throws Exception {
		mockMvc.perform(get("/producers/{id}", producerId)
								.header("Authorization", adminBearerToken))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(producerId))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("John Smith"));
	}

    @Test
    void getProducerById_returnsExpectedProducer() throws Exception {
        mockMvc.perform(get("/producers/{id}", producerId)
                        .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(producerId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("John Smith"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.profile").value("Award-winning producer."));
    }

    @Test
    void getProducerById_notExistingProducer() throws Exception {
        mockMvc.perform(get("/producers/999999")
                        .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void addProducer_returnsCreatedAndPersistsProducer() throws Exception {
        mockMvc.perform(post("/producers")
                                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Jane Doe",
                          "profile": "Independent producer."
                        }
                        """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Jane Doe"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.profile").value("Independent producer."));

        org.junit.jupiter.api.Assertions.assertTrue(producerRepository.findByName("Jane Doe").isPresent());
    }

    @Test
    void addProducer_whenNameAlreadyExists_returnsConflict() throws Exception {
        mockMvc.perform(post("/producers")
                                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "John Smith",
                          "profile": "Another profile."
                        }
                        """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isConflict())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value("Producer with name John Smith already exists"));
    }

    @Test
    void updateProducer_returnsUpdatedProducerAndPersistsChange() throws Exception {
        mockMvc.perform(put("/producers/{id}", producerId)
                                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "John Smith",
                          "profile": "Updated producer profile."
                        }
                        """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(producerId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("John Smith"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.profile").value("Updated producer profile."));

        mockMvc.perform(get("/producers/{id}", producerId)
                        .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.profile").value("Updated producer profile."));
    }

    @Test
    void updateProducer_withMismatchedId_returnsBadRequest() throws Exception {
        Long mismatchedId = producerId + 98;

        mockMvc.perform(put("/producers/{id}", producerId)
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                          "id": %d,
                          "name": "John Smith",
                          "profile": "Award-winning producer."
                        }
                        """, mismatchedId)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value("Path id " + producerId + " does not match request body id " + mismatchedId));
    }

    @Test
    void updateProducer_whenDuplicateNameExists_returnsConflict() throws Exception {
        ProducerEntity secondProducer = createProducer("Jane Doe", "Independent producer.");

        mockMvc.perform(put("/producers/{id}", secondProducer.getId())
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "John Smith",
                          "profile": "Independent producer."
                        }
                        """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isConflict())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value("Producer with name John Smith already exists with id " + producerId));
    }

    @Test
    void getMoviesByProducerId_returnsList() throws Exception {
        mockMvc.perform(get("/producers/{id}/movies", producerId)
                        .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].id").isNumber())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].name").value("Star Wars: Episode IV - A New Hope"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].producerId").value(producerId));
    }

    @Test
    void getMoviesByProducerId_whenProducerNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/producers/{id}/movies", 999999L)
                        .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getMoviesByProducerId_whenProducerHasNoMovies_returnsEmptyArray() throws Exception {
        ProducerEntity producerWithoutMovies = createProducer("Jane Doe", "Independent producer.");

                mockMvc.perform(get("/producers/{id}/movies", producerWithoutMovies.getId())
                                                .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

        private UserEntity createUser(String username, String password, Role role) {
                UserEntity user = new UserEntity(null, username, password);
                user.setRole(role);
                user.setNew(true);
                return userRepository.save(user);
        }

        private String loginAndGetToken(String username, String password) {
                try {
                        MvcResult result = mockMvc.perform(post("/auth/login")
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .content(String.format("""
                                                                        {
                                                                          \"username\": \"%s\",
                                                                          \"password\": \"%s\"
                                                                        }
                                                                        """, username, password)))
                                        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                                        .andReturn();
                        return "Bearer " + result.getResponse().getContentAsString();
                } catch (Exception e) {
                        throw new IllegalStateException("Unable to authenticate test user", e);
                }
        }

    private ProducerEntity createProducer(String name, String profile) {
        ProducerEntity producer = new ProducerEntity(null, name, profile);
        producer.setNew(true);
        return producerRepository.save(producer);
    }

    private MovieEntity createMovieForProducer(ProducerEntity producer) {
        MovieEntity movie = new MovieEntity(
                null,
                "Star Wars: Episode IV - A New Hope",
                LocalDate.of(1977, 5, 25),
                121,
                Rating.PG,
                "Luke Skywalker begins his journey as a Jedi Knight...");
        movie.setProducer(producer);
        movie.setNew(true);
        return movieRepository.save(movie);
    }
}
