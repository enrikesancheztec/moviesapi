package com.kikesoft.moviesapi.mapper;

import com.kikesoft.moviesapi.entity.MovieEntity;
import com.kikesoft.moviesapi.vo.MovieVO;

/**
 * Utility mapper for converting between persistence entities and API value objects.
 *
 * @author Enrique Sanchez
 */
public final class MovieMapper {
    /**
     * Utility class constructor.
     */
    private MovieMapper() {
    }

    /**
     * Converts a {@link MovieEntity} into a {@link MovieVO}.
     * Returns {@code null} when the input is {@code null}.
     *
     * @param entity persistence entity
     * @return value object representation or {@code null}
     */
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

    /**
     * Converts a {@link MovieVO} into a {@link MovieEntity}.
     * Returns {@code null} when the input is {@code null}.
     *
     * @param movieVO value object
     * @return persistence entity representation or {@code null}
     */
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
