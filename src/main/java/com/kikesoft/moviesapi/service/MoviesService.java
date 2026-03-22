package com.kikesoft.moviesapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kikesoft.moviesapi.dao.MoviesDAO;
import com.kikesoft.moviesapi.vo.MovieVO;

@Service
public class MoviesService {
    @Autowired
    private MoviesDAO moviesDAO;

    public Optional<MovieVO> findById(Long id) {
        return moviesDAO.findById(id);
    }

    public List<MovieVO> findAll() {
        return moviesDAO.findAll();
    }
}
