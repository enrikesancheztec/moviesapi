package com.kikesoft.moviesapi.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kikesoft.moviesapi.entity.MovieEntity;

/**
 * Repository for CRUD operations over {@link MovieEntity}.
 *
 * @author Enrique Sanchez
 */
@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
    /**
     * Finds a movie by title and release date.
     *
     * @param name movie title
     * @param launchDate movie release date
     * @return optional containing the matching movie entity when found
     */
    Optional<MovieEntity> findByNameAndLaunchDate(String name, LocalDate launchDate);
}
