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
class MoviesControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

        @Autowired
        private ProducerRepository producerRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

    private Long movieId;
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
                movieId = createMovieOne(producer).getId();
        }

        @Test
        void getMovieById_withoutToken_returnsUnauthorized() throws Exception {
                mockMvc.perform(get("/movies/{id}", movieId))
                                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
    }

        @Test
	void getMovieById_withAdminRole_succeeds() throws Exception {
		mockMvc.perform(get("/movies/{id}", movieId)
								.header("Authorization", adminBearerToken))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(movieId))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Star Wars: Episode IV - A New Hope"));
	}

    @Test
    void getMovieById_returnsExpectedMovie() throws Exception {
        mockMvc.perform(get("/movies/{id}", movieId)
                        .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(movieId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Star Wars: Episode IV - A New Hope"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.launchDate").value("1977-05-25"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.duration").value(121))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.rating").value("PG"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.producerId").value(producerId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.producer.id").value(producerId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.producer.name").value("John Smith"));
    }

    @Test
    void getMovieById_notExistingMovie() throws Exception {
        mockMvc.perform(get("/movies/999999")
                        .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateMovie_returnsUpdatedMovieAndPersistsChange() throws Exception {
        mockMvc.perform(put("/movies/{id}", movieId)
                                .header("Authorization", bearerToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("""
                {
                  "name": "Star Wars: Episode IV - A New Hope (Remastered)",
                  "launchDate": "1977-05-25",
                  "duration": 124,
                  "rating": "PG",
                  "description": "Updated integration test description."
                }
                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(movieId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name")
                        .value("Star Wars: Episode IV - A New Hope (Remastered)"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.duration").value(124))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.description")
                        .value("Updated integration test description."))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.producerId").value(producerId));

        mockMvc.perform(get("/movies/{id}", movieId)
                        .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(movieId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name")
                        .value("Star Wars: Episode IV - A New Hope (Remastered)"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.duration").value(124))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.description")
                        .value("Updated integration test description."))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.producerId").value(producerId));
    }

    @Test
    void addMovie_withProducerId_returnsCreatedAndPersistsAssociation() throws Exception {
        mockMvc.perform(post("/movies")
                                .header("Authorization", bearerToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(String.format("""
                {
                  "name": "Inception",
                  "launchDate": "2010-07-16",
                  "duration": 148,
                  "rating": "PG_13",
                  "description": "A thief enters dreams to steal corporate secrets.",
                  "producerId": %d
                }
                """, producerId)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Inception"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.producerId").value(producerId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.producer.id").value(producerId));
    }

    @Test
    void addMovie_withoutProducerId_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/movies")
                                .header("Authorization", bearerToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("""
                {
                  "name": "Inception",
                  "launchDate": "2010-07-16",
                  "duration": 148,
                  "rating": "PG_13",
                  "description": "A thief enters dreams to steal corporate secrets."
                }
                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value("producerId is required when creating a movie"));
    }

    @Test
    void updateMovie_withMismatchedId_returnsBadRequest() throws Exception {
        Long mismatchedId = movieId + 98;

        mockMvc.perform(put("/movies/{id}", movieId)
                .header("Authorization", bearerToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(String.format("""
                {
                  "id": %d,
                  "name": "Star Wars: Episode IV - A New Hope",
                  "launchDate": "1977-05-25",
                  "duration": 121,
                  "rating": "PG",
                  "description": "Luke Skywalker begins his journey as a Jedi Knight..."
                }
                """, mismatchedId))
        )
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value("Path id " + movieId + " does not match request body id " + mismatchedId));
    }

    @Test
    void getAllMovies_withoutProducerIdParam_returnsAllMovies() throws Exception {
        mockMvc.perform(get("/movies")
                        .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].id").value(movieId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].name")
                        .value("Star Wars: Episode IV - A New Hope"));
    }

    @Test
    void getAllMovies_withValidProducerIdParam_returnsFilteredMovies() throws Exception {
        mockMvc.perform(get("/movies?producerId={producerId}", producerId)
                        .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].id").value(movieId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].producerId").value(producerId));
    }

    @Test
    void getAllMovies_withInvalidProducerIdParam_returnsNotFound() throws Exception {
        mockMvc.perform(get("/movies?producerId=999999")
                        .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getAllMovies_withValidProducerIdButNoMovies_returnsEmptyArray() throws Exception {
        ProducerEntity producerWithoutMovies = createProducer("Jane Doe", "Independent producer.");

        mockMvc.perform(get("/movies?producerId={producerId}", producerWithoutMovies.getId())
                        .header("Authorization", bearerToken))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

        private MovieEntity createMovieOne(ProducerEntity producer) {
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

        private ProducerEntity createProducer(String name, String profile) {
                ProducerEntity producer = new ProducerEntity(null, name, profile);
                producer.setNew(true);
                return producerRepository.save(producer);
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
}
