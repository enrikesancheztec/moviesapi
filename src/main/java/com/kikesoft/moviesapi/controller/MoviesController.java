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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller that exposes movie operations.
 *
 * @author Enrique Sanchez
 */
@RestController
@RequestMapping("/movies")
@Tag(name = "Movies", description = "Operations to retrieve, create and update movies")
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
        @Operation(summary = "Get movie by id", description = "Returns a movie when the identifier exists")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie found", content = @Content(schema = @Schema(implementation = MovieVO.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-19T10:00:00\",\"message\":\"Movie with id 99 not found\"}")))
        })
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
    @Operation(summary = "Get all movies", description = "Returns all movies currently stored")
    @ApiResponse(responseCode = "200", description = "Movies retrieved", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MovieVO.class))))
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
    @Operation(summary = "Create movie", description = "Creates a movie when the payload is valid and not duplicated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie created", content = @Content(schema = @Schema(implementation = MovieVO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"name\":\"Name is mandatory\"}"))),
            @ApiResponse(responseCode = "409", description = "Movie already exists", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-19T10:00:00\",\"message\":\"Movie with name Inception and launch date 2010-07-16 already exists\"}")))
    })
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
    @Operation(summary = "Update movie", description = "Updates an existing movie by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie updated", content = @Content(schema = @Schema(implementation = MovieVO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payload or id mismatch", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-19T10:00:00\",\"message\":\"Path id 3 does not match request body id 99\"}"))),
            @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-19T10:00:00\",\"message\":\"Movie with id 3 not found\"}"))),
            @ApiResponse(responseCode = "409", description = "Movie duplicated by name and launch date", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-19T10:00:00\",\"message\":\"Movie with name Inception and launch date 2010-07-16 already exists with id 7\"}")))
    })
    ResponseEntity<MovieVO> update(@PathVariable Long id, @Valid @RequestBody MovieVO movieVO) {
        MovieVO updatedMovie = moviesService.update(id, movieVO);
        if (updatedMovie == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updatedMovie);
    }
}
