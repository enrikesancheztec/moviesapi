package com.kikesoft.moviesapi.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kikesoft.moviesapi.entity.ProducerEntity;
import com.kikesoft.moviesapi.vo.ProducerVO;

/**
 * Utility mapper for converting between persistence entities and API value objects.
 *
 * @author Enrique Sanchez
 */
public final class ProducerMapper {
    private static final Logger LOGGER = LogManager.getLogger(ProducerMapper.class);

    /**
     * Utility class constructor.
     */
    private ProducerMapper() {
    }

    /**
     * Converts a {@link ProducerEntity} into a {@link ProducerVO}.
     * Returns {@code null} when the input is {@code null}.
     *
     * @param entity persistence entity
     * @return value object representation or {@code null}
     */
    public static ProducerVO toVO(ProducerEntity entity) {
        if (entity == null) {
            LOGGER.debug("Mapper toVO - source entity is null");
            return null;
        }

        LOGGER.debug("Mapper toVO - mapping entity id={}", entity.getId());

        return new ProducerVO(
                entity.getId(),
                entity.getName(),
                entity.getProfile()
        );
    }

    /**
     * Converts a {@link ProducerVO} into a {@link ProducerEntity}.
     * Returns {@code null} when the input is {@code null}.
     *
     * @param producerVO value object
     * @return persistence entity representation or {@code null}
     */
    public static ProducerEntity toEntity(ProducerVO producerVO) {
        if (producerVO == null) {
            LOGGER.debug("Mapper toEntity - source producerVO is null");
            return null;
        }

        LOGGER.debug("Mapper toEntity - mapping producerVO id={}", producerVO.getId());

        return new ProducerEntity(
                producerVO.getId(),
                producerVO.getName(),
                producerVO.getProfile()
        );
    }
}