package com.kikesoft.moviesapi.repository;

import java.time.LocalDate;
import java.util.List;
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

    /**
     * Finds all movies associated with a producer.
     *
     * @param producerId producer identifier
     * @return list of movie entities for the producer
     */
    List<MovieEntity> findByProducerId(Long producerId);
}
