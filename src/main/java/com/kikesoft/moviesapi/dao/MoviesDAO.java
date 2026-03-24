package com.kikesoft.moviesapi.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kikesoft.moviesapi.mapper.MovieMapper;
import com.kikesoft.moviesapi.repository.MovieRepository;
import com.kikesoft.moviesapi.vo.MovieVO;

@Repository
public class MoviesDAO {
    @Autowired
    private MovieRepository movieRepository;

    public Optional<MovieVO> findById(Long id) {
        return movieRepository.findById(id).map(MovieMapper::toVO);
    }

    public List<MovieVO> findAll() {
        return movieRepository.findAll().stream().map(MovieMapper::toVO).toList();
    }

    public MovieVO save(MovieVO movieVO) {
        return MovieMapper.toVO(movieRepository.save(MovieMapper.toEntity(movieVO)));
    }

    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }
}
