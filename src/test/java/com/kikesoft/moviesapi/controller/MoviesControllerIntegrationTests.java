package com.kikesoft.moviesapi.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.kikesoft.moviesapi.entity.MovieEntity;
import com.kikesoft.moviesapi.enumeration.Rating;
import com.kikesoft.moviesapi.repository.MovieRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MoviesControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    private Long movieId;

    @BeforeEach
    void setUp() {
        movieId = createMovieOne().getId();
    }

    @Test
    void getMovieById_returnsExpectedMovie() throws Exception {
        mockMvc.perform(get("/movies/{id}", movieId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(movieId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Star Wars: Episode IV - A New Hope"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.launchDate").value("1977-05-25"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.duration").value(121))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.rating").value("PG"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.description").exists());
    }

    @Test
    void getMovieById_notExistingMovie() throws Exception {
        mockMvc.perform(get("/movies/999999"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateMovie_returnsUpdatedMovieAndPersistsChange() throws Exception {
        mockMvc.perform(put("/movies/{id}", movieId)
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
                        .value("Updated integration test description."));

        mockMvc.perform(get("/movies/{id}", movieId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(movieId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name")
                        .value("Star Wars: Episode IV - A New Hope (Remastered)"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.duration").value(124))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.description")
                        .value("Updated integration test description."));
    }

    @Test
    void updateMovie_withMismatchedId_returnsBadRequest() throws Exception {
        Long mismatchedId = movieId + 98;

        mockMvc.perform(put("/movies/{id}", movieId)
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

    private MovieEntity createMovieOne() {
        movieRepository.deleteAll();
        MovieEntity movie = new MovieEntity(
                null,
                "Star Wars: Episode IV - A New Hope",
                LocalDate.of(1977, 5, 25),
                121,
                Rating.PG,
                "Luke Skywalker begins his journey as a Jedi Knight...");
        movie.setNew(true);
        return movieRepository.save(movie);
    }
}
