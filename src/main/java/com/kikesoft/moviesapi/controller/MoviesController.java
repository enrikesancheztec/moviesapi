package com.kikesoft.moviesapi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kikesoft.moviesapi.service.MoviesService;
import com.kikesoft.moviesapi.vo.MovieVO;

@RestController
@RequestMapping("/movies")
class MoviesController {
    @Autowired
    MoviesService moviesService;

    @GetMapping("/{id}")
    ResponseEntity<MovieVO> getById(@PathVariable Long id) {
        Optional<MovieVO> movie = moviesService.findById(id);

        if (movie.isPresent()) {
            return ResponseEntity.ok(movie.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    ResponseEntity<List<MovieVO>> getAll() {
        return ResponseEntity.ok(moviesService.findAll());
    }
}
