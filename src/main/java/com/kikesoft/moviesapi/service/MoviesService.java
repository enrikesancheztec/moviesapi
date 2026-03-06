package com.kikesoft.moviesapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kikesoft.moviesapi.enumeration.Rating;
import com.kikesoft.moviesapi.vo.MovieVO;

@Service
public class MoviesService {
    public Optional<MovieVO> findById(Long id) {
        if (id == 1) {
            MovieVO movie = new MovieVO(1L,
                    "Star Wars: Episode IV - A New Hope",
                    LocalDate.of(1977, 5, 25),
                    121,
                    Rating.PG,
                    "Luke Skywalker begins his journey as a Jedi Knight...");
            return Optional.of(movie);
        } else {
            return Optional.empty();
        }
    }

    public List<MovieVO> findAll() {
        List<MovieVO> movies = List.of(
                new MovieVO(1L,
                        "Star Wars: Episode IV - A New Hope",
                        LocalDate.of(1977, 5, 25),
                        121,
                        Rating.PG,
                        "Luke Skywalker begins his journey as a Jedi Knight..."),
                new MovieVO(2L,
                        "The Godfather",
                        LocalDate.of(1972, 3, 24),
                        175,
                        Rating.R,
                        "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son...")
                    );
        // Implementation for finding all movies
        return movies;
    }
}
