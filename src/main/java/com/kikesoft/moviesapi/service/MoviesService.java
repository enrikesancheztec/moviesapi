package com.kikesoft.moviesapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kikesoft.moviesapi.dao.MoviesDAO;
import com.kikesoft.moviesapi.vo.MovieVO;

/**
 * Service layer that exposes movie read operations and delegates persistence access to the DAO.
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
     * @return persisted movie representation or {@code null} when the input is {@code null}
     */
    public MovieVO add(MovieVO movieVO) {
        return moviesDAO.save(movieVO);
    }
}
