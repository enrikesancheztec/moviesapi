package com.kikesoft.moviesapi.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kikesoft.moviesapi.entity.MovieEntity;
import com.kikesoft.moviesapi.vo.MovieVO;

/**
 * Utility mapper for converting between persistence entities and API value objects.
 *
 * @author Enrique Sanchez
 */
public final class MovieMapper {
    private static final Logger LOGGER = LogManager.getLogger(MovieMapper.class);

    /**
     * Utility class constructor.
     */
    private MovieMapper() {
    }

    /**
     * Converts a {@link MovieEntity} into a {@link MovieVO}.
     * Returns {@code null} when the input is {@code null}.
        * Maps producer details into {@code producerId} and nested {@code producer}
        * when available.
     *
     * @param entity persistence entity
     * @return value object representation or {@code null}
     */
    public static MovieVO toVO(MovieEntity entity) {
        if (entity == null) {
            LOGGER.debug("Mapper toVO - source entity is null");
            return null;
        }

        LOGGER.debug("Mapper toVO - mapping entity id={}", entity.getId());

        MovieVO movieVO = new MovieVO(
                entity.getId(),
                entity.getName(),
                entity.getLaunchDate(),
                entity.getDuration(),
                entity.getRating(),
                entity.getDescription()
        );

        if (entity.getProducer() != null) {
            movieVO.setProducerId(entity.getProducer().getId());
            movieVO.setProducer(ProducerMapper.toVO(entity.getProducer()));
        }

        return movieVO;
    }

    /**
     * Converts a {@link MovieVO} into a {@link MovieEntity}.
     * Returns {@code null} when the input is {@code null}.
        * Producer association is resolved in DAO layer using {@code producerId}.
     *
     * @param movieVO value object
     * @return persistence entity representation or {@code null}
     */
    public static MovieEntity toEntity(MovieVO movieVO) {
        if (movieVO == null) {
            LOGGER.debug("Mapper toEntity - source movieVO is null");
            return null;
        }

        LOGGER.debug("Mapper toEntity - mapping movieVO id={}", movieVO.getId());

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
