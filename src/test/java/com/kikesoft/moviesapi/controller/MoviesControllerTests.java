package com.kikesoft.moviesapi.controller;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.kikesoft.moviesapi.enumeration.Rating;
import com.kikesoft.moviesapi.exception.ItemIdMismatchException;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.service.MoviesService;
import com.kikesoft.moviesapi.vo.MovieVO;

@WebMvcTest(MoviesController.class)
class MoviesControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MoviesService moviesService;

    @Test
    void getMovieById_returnsExpectedMovie() throws Exception {
        when(moviesService.findById(1L)).thenReturn(
                new MovieVO(
                        1L,
                        "Star Wars: Episode IV - A New Hope",
                        LocalDate.of(1977, 5, 25),
                        121,
                        Rating.PG,
                        "Luke Skywalker begins his journey as a Jedi Knight..."));

        mockMvc.perform(get("/movies/1"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Star Wars: Episode IV - A New Hope"));
    }

    @Test
    void getMovieById_notExistingMovie() throws Exception {
        when(moviesService.findById(2L)).thenThrow(new ItemNotFoundException("Movie with id 2 not found"));

        mockMvc.perform(get("/movies/2"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getAll_returnsMovieList() throws Exception {
        when(moviesService.findAll()).thenReturn(List.of(
                new MovieVO(
                        1L,
                        "Star Wars: Episode IV - A New Hope",
                        LocalDate.of(1977, 5, 25),
                        121,
                        Rating.PG,
                        "Luke Skywalker begins his journey as a Jedi Knight..."),
                new MovieVO(
                        2L,
                        "The Godfather",
                        LocalDate.of(1972, 3, 24),
                        175,
                        Rating.R,
                        "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son...")));

        mockMvc.perform(get("/movies"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[1].id").value(2));
    }

    @Test
    void addNewMovie_withoutId_returnsCreated() throws Exception {
        when(moviesService.add(argThat(movie -> movie != null && movie.getId() == null))).thenReturn(
                new MovieVO(
                        3L,
                        "Inception",
                        LocalDate.of(2010, 7, 16),
                        148,
                        Rating.PG_13,
                        "A thief enters dreams to steal corporate secrets."
                )
        );

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Inception",
                                  "launchDate": "2010-07-16",
                                  "duration": 148,
                                  "rating": "PG_13",
                                  "description": "A thief enters dreams to steal corporate secrets."
                                }
                                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(3))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Inception"));
    }

    @Test
    void addNewMovie_withId_returnsBadRequest() throws Exception {
        when(moviesService.add(argThat(movie -> movie != null && movie.getId() != null))).thenReturn(null);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 99,
                                  "name": "Inception",
                                  "launchDate": "2010-07-16",
                                  "duration": 148,
                                  "rating": "PG_13",
                                  "description": "A thief enters dreams to steal corporate secrets."
                                }
                                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateMovie_returnsOk() throws Exception {
        when(moviesService.update(argThat(id -> id != null && id.equals(3L)),
                argThat(movie -> movie != null && movie.getId() == null && "Inception".equals(movie.getName()))))
                .thenReturn(new MovieVO(
                        3L,
                        "Inception",
                        LocalDate.of(2010, 7, 16),
                        148,
                        Rating.PG_13,
                        "A thief enters dreams to steal corporate secrets."
                ));

        mockMvc.perform(put("/movies/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Inception",
                                  "launchDate": "2010-07-16",
                                  "duration": 148,
                                  "rating": "PG_13",
                                  "description": "A thief enters dreams to steal corporate secrets."
                                }
                                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(3))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Inception"));
    }

    @Test
    void updateMovie_withMismatchedId_returnsBadRequest() throws Exception {
        when(moviesService.update(argThat(id -> id != null && id.equals(3L)),
                argThat(movie -> movie != null && Long.valueOf(99L).equals(movie.getId()))))
                .thenThrow(new ItemIdMismatchException("Path id 3 does not match request body id 99"));

        mockMvc.perform(put("/movies/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 99,
                                  "name": "Inception",
                                  "launchDate": "2010-07-16",
                                  "duration": 148,
                                  "rating": "PG_13",
                                  "description": "A thief enters dreams to steal corporate secrets."
                                }
                                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value("Path id 3 does not match request body id 99"));
    }
}
