package com.kikesoft.moviesapi.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kikesoft.moviesapi.entity.MovieEntity;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.mapper.MovieMapper;
import com.kikesoft.moviesapi.repository.MovieRepository;
import com.kikesoft.moviesapi.vo.MovieVO;

/**
 * Data access component that coordinates movie persistence operations.
 *
 * @author Enrique Sanchez
 */
@Repository
public class MoviesDAO {
    /**
     * Repository dependency used for movie persistence operations.
     */
    @Autowired
    private MovieRepository movieRepository;

    /**
     * Finds a movie by id.
     *
     * @param id movie identifier
     * @return optional movie representation
     */
    public MovieVO findById(Long id) {
        Optional<MovieVO> movieVO = movieRepository.findById(id).map(MovieMapper::toVO);

        if (movieVO.isEmpty()) {
            throw new ItemNotFoundException("Movie with id " + id + " not found");
        }

        return movieVO.get();
    }

    /**
     * Retrieves all movies currently stored.
     *
     * @return list of movies
     */
    public List<MovieVO> findAll() {
        return movieRepository.findAll().stream().map(MovieMapper::toVO).toList();
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
        Optional<MovieEntity> movieEntity = movieRepository.findByNameAndLaunchDate(name, launchDate);
        
        if (movieEntity.isEmpty()) {
            throw new ItemNotFoundException("Movie with name '" + name + "' and launch date " + launchDate + " not found");
        }
        
        return MovieMapper.toVO(movieEntity.get());
    }

    /**
     * Persists a movie and returns the stored representation.
     * This method supports both create and update operations.
     *
     * @param movieVO movie to persist
     * @return persisted movie or {@code null} when the input is {@code null}
     */
    public MovieVO save(MovieVO movieVO) {
        MovieEntity movieEntity = MovieMapper.toEntity(movieVO);
        if (movieEntity == null) {
            return null;
        }
        if (movieEntity.getId() == null) {
            movieEntity.setNew(true);
        }
        return MovieMapper.toVO(movieRepository.save(movieEntity));
    }

    /**
     * Deletes a movie by id.
     *
     * @param id movie identifier
     */
    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }
}
