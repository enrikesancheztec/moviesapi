package com.kikesoft.moviesapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kikesoft.moviesapi.dao.MoviesDAO;
import com.kikesoft.moviesapi.exception.DuplicatedItemException;
import com.kikesoft.moviesapi.exception.ItemIdMismatchException;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.vo.MovieVO;

/**
 * Service layer that exposes movie read operations and delegates persistence
 * access to the DAO.
 *
 * @author Enrique Sanchez
 */
@Service
public class MoviesService {

    /**
     * DAO dependency used to access movie data.
     */
    @Autowired
    private MoviesDAO moviesDAO;

    /**
     * Finds a movie by id.
     *
     * @param id movie identifier
     * @return optional movie representation
     */
    public MovieVO findById(Long id) {
        return moviesDAO.findById(id);
    }

    /**
     * Retrieves all movies available in the persistence layer.
     *
     * @return list of movies
     */
    public List<MovieVO> findAll() {
        return moviesDAO.findAll();
    }

    /**
     * Adds a new movie to the persistence layer.
     *
     * @param movieVO movie payload to persist
     * @return persisted movie representation or {@code null} when the input is
     * {@code null}
     */
    public MovieVO add(MovieVO movieVO) {
        try {
            moviesDAO.findByNameAndLaunchDate(movieVO.getName(), movieVO.getLaunchDate());
            throw new DuplicatedItemException("Movie with name " + movieVO.getName()
                    + " and launch date " + movieVO.getLaunchDate() + " already exists");
        } catch (final ItemNotFoundException infe) {
            // Movie not found, proceed with creation
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
        if (movieVO.getId() != null && !movieVO.getId().equals(id)) {
            throw new ItemIdMismatchException(
                    "Path id " + id + " does not match request body id " + movieVO.getId());
        }

        // Ensure target entity exists before attempting update.
        moviesDAO.findById(id);

        try {
            MovieVO foundMovie = moviesDAO.findByNameAndLaunchDate(movieVO.getName(), movieVO.getLaunchDate());
            if (foundMovie.getId() != null && !foundMovie.getId().equals(id)) {
                throw new DuplicatedItemException("Movie with name " + movieVO.getName()
                        + " and launch date " + movieVO.getLaunchDate() + " already exists with id "
                        + foundMovie.getId());
            }
        } catch (final ItemNotFoundException infe) {
            // No duplicate found by name and launch date, proceed with update.
        }

        movieVO.setId(id);
        return moviesDAO.update(movieVO);
    }
}
