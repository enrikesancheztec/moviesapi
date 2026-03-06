package com.kikesoft.moviesapi.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kikesoft.moviesapi.enumeration.Rating;
import com.kikesoft.moviesapi.vo.MovieVO;

@RestController
@RequestMapping("/movies")
class MoviesController {

    @GetMapping("/{id}")
    ResponseEntity<MovieVO> getById(@PathVariable Long id) {
        if (id == 1) {
            MovieVO movie = new MovieVO(1L,
                    "Star Wars: Episode IV - A New Hope",
                    LocalDate.of(1977, 5, 25),
                    121,
                    Rating.PG,
                    "Luke Skywalker begins his journey as a Jedi Knight...");
            return ResponseEntity.ok(movie);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
