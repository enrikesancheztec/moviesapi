package com.kikesoft.moviesapi.mapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import com.kikesoft.moviesapi.entity.MovieEntity;
import com.kikesoft.moviesapi.entity.ProducerEntity;
import com.kikesoft.moviesapi.enumeration.Rating;
import com.kikesoft.moviesapi.vo.MovieVO;

class MovieMapperTests {

    @Test
    void toVO_whenEntityIsNull_returnsNull() {
        MovieVO result = MovieMapper.toVO(null);

        assertNull(result);
    }

    @Test
    void toVO_whenEntityIsNotNull_mapsAllFields() {
        MovieEntity entity = new MovieEntity(
                1L,
                "Star Wars: Episode IV - A New Hope",
                LocalDate.of(1977, 5, 25),
                121,
                Rating.PG,
                "Luke Skywalker begins his journey as a Jedi Knight...");

        MovieVO result = MovieMapper.toVO(entity);

        assertEquals(1L, result.getId());
        assertEquals("Star Wars: Episode IV - A New Hope", result.getName());
        assertEquals(LocalDate.of(1977, 5, 25), result.getLaunchDate());
        assertEquals(121, result.getDuration());
        assertEquals(Rating.PG, result.getRating());
        assertEquals("Luke Skywalker begins his journey as a Jedi Knight...", result.getDescription());
    }

    @Test
    void toVO_whenEntityHasProducer_mapsProducerFields() {
        ProducerEntity producer = new ProducerEntity(10L, "John Smith", "Award-winning producer.");
        MovieEntity entity = new MovieEntity(
                1L,
                "Star Wars: Episode IV - A New Hope",
                LocalDate.of(1977, 5, 25),
                121,
                Rating.PG,
                "Luke Skywalker begins his journey as a Jedi Knight...",
                producer);

        MovieVO result = MovieMapper.toVO(entity);

        assertEquals(10L, result.getProducerId());
        assertEquals(10L, result.getProducer().getId());
        assertEquals("John Smith", result.getProducer().getName());
        assertEquals("Award-winning producer.", result.getProducer().getProfile());
    }

    @Test
    void toEntity_whenMovieVOIsNull_returnsNull() {
        MovieEntity result = MovieMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void toEntity_whenMovieVOIsNotNull_mapsAllFields() {
        MovieVO movieVO = new MovieVO(
                2L,
                "The Godfather",
                LocalDate.of(1972, 3, 24),
                175,
                Rating.R,
                "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son...");

        MovieEntity result = MovieMapper.toEntity(movieVO);

        assertEquals(2L, result.getId());
        assertEquals("The Godfather", result.getName());
        assertEquals(LocalDate.of(1972, 3, 24), result.getLaunchDate());
        assertEquals(175, result.getDuration());
        assertEquals(Rating.R, result.getRating());
        assertEquals("The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son...", result.getDescription());
    }

    @Test
    void toEntity_whenMovieVOHasProducerFields_ignoresProducerMapping() {
        MovieVO movieVO = new MovieVO(
                2L,
                "The Godfather",
                LocalDate.of(1972, 3, 24),
                175,
                Rating.R,
                "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son...");
        movieVO.setProducerId(20L);

        MovieEntity result = MovieMapper.toEntity(movieVO);

        assertNull(result.getProducer());
    }
}
