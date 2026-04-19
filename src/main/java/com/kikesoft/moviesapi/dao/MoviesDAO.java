package com.kikesoft.moviesapi.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kikesoft.moviesapi.entity.MovieEntity;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.mapper.MovieMapper;
import com.kikesoft.moviesapi.repository.MovieRepository;
import com.kikesoft.moviesapi.repository.ProducerRepository;
import com.kikesoft.moviesapi.vo.MovieVO;

/**
 * Data access component that coordinates movie persistence operations.
 *
 * @author Enrique Sanchez
 */
@Repository
public class MoviesDAO {
    private static final Logger LOGGER = LogManager.getLogger(MoviesDAO.class);

    /**
     * Repository dependency used for movie persistence operations.
     */
    @Autowired
    private MovieRepository movieRepository;

    /**
     * Repository dependency used for producer lookups.
     */
    @Autowired
    private ProducerRepository producerRepository;

    /**
     * Finds a movie by id.
     *
     * @param id movie identifier
        * @return movie representation
     */
    public MovieVO findById(Long id) {
        LOGGER.debug("DAO findById - id={}", id);
        Optional<MovieVO> movieVO = movieRepository.findById(id).map(MovieMapper::toVO);

        if (movieVO.isEmpty()) {
            LOGGER.warn("DAO findById - movie not found for id={}", id);
            throw new ItemNotFoundException("Movie with id " + id + " not found");
        }

        LOGGER.debug("DAO findById - movie found for id={}", id);
        return movieVO.get();
    }

    /**
     * Retrieves all movies currently stored.
     *
     * @return list of movies
     */
    public List<MovieVO> findAll() {
        LOGGER.debug("DAO findAll - fetching all movies");
        List<MovieVO> movies = movieRepository.findAll().stream().map(MovieMapper::toVO).toList();
        LOGGER.debug("DAO findAll - retrieved {} movies", movies.size());
        return movies;
    }

    /**
     * Finds a movie by name and launch date.
     *
     * @param name movie title
     * @param launchDate release date
     * @return movie representation
     * @throws ItemNotFoundException if no movie is found with the given name and launch date
     */
    public MovieVO findByNameAndLaunchDate(final String name, final LocalDate launchDate) {
        LOGGER.debug("DAO findByNameAndLaunchDate - name='{}', launchDate='{}'", name, launchDate);
        Optional<MovieEntity> movieEntity = movieRepository.findByNameAndLaunchDate(name, launchDate);
        
        if (movieEntity.isEmpty()) {
            LOGGER.warn("DAO findByNameAndLaunchDate - movie not found for name='{}', launchDate='{}'", name,
                    launchDate);
            throw new ItemNotFoundException("Movie with name '" + name + "' and launch date " + launchDate + " not found");
        }

        LOGGER.debug("DAO findByNameAndLaunchDate - movie found for name='{}', launchDate='{}'", name, launchDate);
        return MovieMapper.toVO(movieEntity.get());
    }

    /**
     * Persists a new movie and returns the stored representation.
     *
     * @param movieVO movie to persist
     * @return persisted movie or {@code null} when the input is {@code null}
     */
    public MovieVO add(MovieVO movieVO) {
        LOGGER.debug("DAO add - mapping and persisting new movie");
        if (movieVO == null) {
            LOGGER.warn("DAO add - input movie is null, nothing to persist");
            return null;
        }

        MovieEntity movieEntity = MovieMapper.toEntity(movieVO);
        if (movieEntity == null) {
            LOGGER.warn("DAO add - input movie is null, nothing to persist");
            return null;
        }

        if (movieVO.getProducerId() != null) {
            movieEntity.setProducer(producerRepository.findById(movieVO.getProducerId())
                    .orElseThrow(() -> new ItemNotFoundException(
                            "Producer with id " + movieVO.getProducerId() + " not found")));
        }

        movieEntity.setNew(true);
        MovieVO saved = MovieMapper.toVO(movieRepository.save(movieEntity));
        LOGGER.debug("DAO add - movie persisted with id={}", saved != null ? saved.getId() : null);
        return saved;
    }

    /**
     * Persists updates for an existing movie and returns the stored representation.
     *
     * @param movieVO movie to update
     * @return updated movie or {@code null} when the input is {@code null}
     */
    public MovieVO update(MovieVO movieVO) {
        LOGGER.debug("DAO update - mapping and persisting movie update for id={}", movieVO != null ? movieVO.getId() : null);
        if (movieVO == null) {
            LOGGER.warn("DAO update - input movie is null, nothing to persist");
            return null;
        }

        MovieEntity movieEntity = MovieMapper.toEntity(movieVO);
        if (movieEntity == null) {
            LOGGER.warn("DAO update - input movie is null, nothing to persist");
            return null;
        }

        if (movieVO.getProducerId() != null) {
            movieEntity.setProducer(producerRepository.findById(movieVO.getProducerId())
                    .orElseThrow(() -> new ItemNotFoundException(
                            "Producer with id " + movieVO.getProducerId() + " not found")));
        } else if (movieVO.getId() != null) {
            movieRepository.findById(movieVO.getId())
                .ifPresent(existingMovie -> movieEntity.setProducer(existingMovie.getProducer()));
        }

        MovieVO updated = MovieMapper.toVO(movieRepository.save(movieEntity));
        LOGGER.debug("DAO update - movie updated with id={}", updated != null ? updated.getId() : null);
        return updated;
    }

    /**
     * Deletes a movie by id.
     *
     * @param id movie identifier
     */
    public void deleteById(Long id) {
        LOGGER.debug("DAO deleteById - deleting movie with id={}", id);
        movieRepository.deleteById(id);
    }
}
