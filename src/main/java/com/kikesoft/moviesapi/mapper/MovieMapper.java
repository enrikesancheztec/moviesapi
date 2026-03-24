package com.kikesoft.moviesapi.mapper;

import com.kikesoft.moviesapi.entity.MovieEntity;
import com.kikesoft.moviesapi.vo.MovieVO;

public final class MovieMapper {
    private MovieMapper() {
    }

    public static MovieVO toVO(MovieEntity entity) {
        if (entity == null) {
            return null;
        }

        return new MovieVO(
                entity.getId(),
                entity.getName(),
                entity.getLaunchDate(),
                entity.getDuration(),
                entity.getRating(),
                entity.getDescription()
        );
    }

    public static MovieEntity toEntity(MovieVO movieVO) {
        if (movieVO == null) {
            return null;
        }

        return new MovieEntity(
                movieVO.getId(),
                movieVO.getName(),
                movieVO.getLaunchDate(),
                movieVO.getDuration(),
                movieVO.getRating(),
                movieVO.getDescription()
        );
    }
}
