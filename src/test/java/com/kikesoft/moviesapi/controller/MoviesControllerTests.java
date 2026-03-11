package com.kikesoft.moviesapi.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.kikesoft.moviesapi.enumeration.Rating;
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
        when(moviesService.findById(1L)).thenReturn(Optional.of(
                new MovieVO(
                        1L,
                        "Star Wars: Episode IV - A New Hope",
                        LocalDate.of(1977, 5, 25),
                        121,
                        Rating.PG,
                        "Luke Skywalker begins his journey as a Jedi Knight...")));

        mockMvc.perform(get("/movies/1"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Star Wars: Episode IV - A New Hope"));
    }

    @Test
    void getMovieById_notExistingMovie() throws Exception {
        when(moviesService.findById(2L)).thenReturn(Optional.empty());

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
}
