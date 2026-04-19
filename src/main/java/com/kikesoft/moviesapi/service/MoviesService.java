package com.kikesoft.moviesapi.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kikesoft.moviesapi.dao.MoviesDAO;
import com.kikesoft.moviesapi.exception.DuplicatedItemException;
import com.kikesoft.moviesapi.exception.ItemIdMismatchException;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.exception.MissingRequiredFieldException;
import com.kikesoft.moviesapi.vo.MovieVO;

/**
 * Service layer that exposes movie read operations and delegates persistence
 * access to the DAO.
 *
 * @author Enrique Sanchez
 */
@Service
public class MoviesService {

    private static final Logger LOGGER = LogManager.getLogger(MoviesService.class);

    /**
     * DAO dependency used to access movie data.
     */
    @Autowired
    private MoviesDAO moviesDAO;

    /**
     * Finds a movie by id.
     *
     * @param id movie identifier
     * @return movie representation
     */
    public MovieVO findById(Long id) {
        LOGGER.debug("Service findById - id={}", id);
        return moviesDAO.findById(id);
    }

    /**
     * Retrieves all movies available in the persistence layer.
     *
     * @return list of movies
     */
    public List<MovieVO> findAll() {
        LOGGER.debug("Service findAll - fetching movies list");
        return moviesDAO.findAll();
    }

    /**
     * Adds a new movie to the persistence layer.
     *
     * @param movieVO movie payload to persist
     * @return persisted movie representation or {@code null} when the input is
     * {@code null}
     * @throws MissingRequiredFieldException when {@code producerId} is missing
     * for movie creation
     */
    public MovieVO add(MovieVO movieVO) {
        if (movieVO.getProducerId() == null) {
            LOGGER.warn("Service add - missing required producerId for movie name='{}'", movieVO.getName());
            throw new MissingRequiredFieldException("producerId is required when creating a movie");
        }

        LOGGER.debug("Service add - validating movie with name='{}' and launchDate='{}'",
                movieVO.getName(), movieVO.getLaunchDate());
        try {
            moviesDAO.findByNameAndLaunchDate(movieVO.getName(), movieVO.getLaunchDate());
            LOGGER.warn("Service add - duplicated movie detected for name='{}' and launchDate='{}'",
                    movieVO.getName(), movieVO.getLaunchDate());
            throw new DuplicatedItemException("Movie with name " + movieVO.getName()
                    + " and launch date " + movieVO.getLaunchDate() + " already exists");
        } catch (final ItemNotFoundException infe) {
            // Movie not found, proceed with creation
            LOGGER.debug("Service add - movie not found by name/date, proceeding with creation");
            return moviesDAO.add(movieVO);
        }
    }

    /**
     * Updates an existing movie in the persistence layer.
     *
     * @param id movie identifier to update
     * @param movieVO movie payload with updated data
     * @return updated movie representation or {@code null} when the input is
     * {@code null}
     * @throws ItemNotFoundException when the movie id does not exist
     * @throws ItemIdMismatchException when path id and payload id do not match
     * @throws DuplicatedItemException when another movie already uses the same
     * name and launch date
     */
    public MovieVO update(final Long id, final MovieVO movieVO) {
        LOGGER.debug("Service update - pathId={}, payloadId={}", id, movieVO.getId());
        if (movieVO.getId() != null && !movieVO.getId().equals(id)) {
            LOGGER.warn("Service update - id mismatch pathId={} payloadId={}", id, movieVO.getId());
            throw new ItemIdMismatchException(
                    "Path id " + id + " does not match request body id " + movieVO.getId());
        }

        // Ensure target entity exists before attempting update.
        LOGGER.debug("Service update - verifying movie exists for id={}", id);
        moviesDAO.findById(id);

        try {
            MovieVO foundMovie = moviesDAO.findByNameAndLaunchDate(movieVO.getName(), movieVO.getLaunchDate());
            if (foundMovie.getId() != null && !foundMovie.getId().equals(id)) {
                LOGGER.warn("Service update - duplicated movie detected for name='{}', launchDate='{}', existingId={}",
                        movieVO.getName(), movieVO.getLaunchDate(), foundMovie.getId());
                throw new DuplicatedItemException("Movie with name " + movieVO.getName()
                        + " and launch date " + movieVO.getLaunchDate() + " already exists with id "
                        + foundMovie.getId());
            }
        } catch (final ItemNotFoundException infe) {
            // No duplicate found by name and launch date, proceed with update.
            LOGGER.debug("Service update - no duplicate found by name/date, proceeding with update");
        }

        movieVO.setId(id);
        LOGGER.debug("Service update - persisting movie id={}", id);
        return moviesDAO.update(movieVO);
    }
}
