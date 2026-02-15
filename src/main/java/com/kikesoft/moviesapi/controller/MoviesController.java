package com.kikesoft.moviesapi.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kikesoft.moviesapi.vo.MovieVO;

@RestController
@RequestMapping("/movies")
public class MoviesController {

    @GetMapping("/{id}")
    public MovieVO getById(@PathVariable Long id) {
        // Mock data de Star Wars
        return new MovieVO(
                1L,
                "Star Wars: Episode IV - A New Hope",
                LocalDate.of(1977, 5, 25),
                121,
                8.6,
                "Luke Skywalker begins his journey as a Jedi Knight under the guidance of Obi-Wan Kenobi, joining forces with Han Solo and Chewbacca to rescue Princess Leia from the evil Galactic Empire and destroy the Death Star."
        );
    }

}
