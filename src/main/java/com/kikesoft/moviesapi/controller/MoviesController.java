package com.kikesoft.moviesapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kikesoft.moviesapi.service.MoviesService;
import com.kikesoft.moviesapi.vo.MovieVO;

import jakarta.validation.Valid;

/**
 * REST controller that exposes movie operations.
 *
 * @author Enrique Sanchez
 */
@RestController
@RequestMapping("/movies")
class MoviesController {
    @Autowired
    MoviesService moviesService;

    /**
     * Retrieves a movie by id.
     *
     * @param id movie identifier
     * @return movie representation
     */
    @GetMapping("/{id}")
    ResponseEntity<MovieVO> getById(@PathVariable Long id) {
            MovieVO movie = moviesService.findById(id);
            return ResponseEntity.ok(movie);
    }

    /**
     * Retrieves all movies.
     *
     * @return list of movies
     */
    @GetMapping
    ResponseEntity<List<MovieVO>> getAll() {
        return ResponseEntity.ok(moviesService.findAll());
    }

    /**
     * Creates a new movie.
     *
     * @param movieVO movie payload to create
     * @return persisted movie representation
     */
    @PostMapping
    ResponseEntity<MovieVO> addNew(@Valid @RequestBody MovieVO movieVO) {
        MovieVO savedMovie = moviesService.add(movieVO);
        if (savedMovie == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
    }

    /**
     * Updates an existing movie.
     *
     * @param id movie identifier
     * @param movieVO movie payload with updated values
     * @return updated movie representation
     */
    @PutMapping("/{id}")
    ResponseEntity<MovieVO> update(@PathVariable Long id, @Valid @RequestBody MovieVO movieVO) {
        MovieVO updatedMovie = moviesService.update(id, movieVO);
        if (updatedMovie == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updatedMovie);
    }
}
