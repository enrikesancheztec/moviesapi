package com.kikesoft.moviesapi.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kikesoft.moviesapi.entity.Role;
import com.kikesoft.moviesapi.entity.UserEntity;
import com.kikesoft.moviesapi.vo.UserVO;

/**
 * Utility mapper for converting between persistence entities and API value objects.
 *
 * @author Enrique Sanchez
 */
public final class UserMapper {
    private static final Logger LOGGER = LogManager.getLogger(UserMapper.class);

    /**
     * Utility class constructor.
     */
    private UserMapper() {
    }

    /**
     * Converts a {@link UserEntity} into a {@link UserVO}.
     * The {@code password} field is deliberately set to {@code null} so it is
     * never included in API responses.
     * Returns {@code null} when the input is {@code null}.
     *
     * @param entity persistence entity
     * @return value object representation with {@code password} set to {@code null},
     *         or {@code null} if the input is {@code null}
     */
    public static UserVO toVO(UserEntity entity) {
        if (entity == null) {
            LOGGER.debug("Mapper toVO - source entity is null");
            return null;
        }

        LOGGER.debug("Mapper toVO - mapping entity id={}", entity.getId());

        return new UserVO(entity.getId(), entity.getUsername(), null, entity.getRole());
    }

    /**
     * Converts a {@link UserVO} into a {@link UserEntity}.
     * Returns {@code null} when the input is {@code null}.
     *
     * @param userVO value object (must contain a non-null password)
     * @return persistence entity representation or {@code null}
     */
    public static UserEntity toEntity(UserVO userVO) {
        if (userVO == null) {
            LOGGER.debug("Mapper toEntity - source userVO is null");
            return null;
        }

        LOGGER.debug("Mapper toEntity - mapping userVO id={}", userVO.getId());

        Role role = userVO.getRole() != null ? userVO.getRole() : Role.USER;
        return new UserEntity(userVO.getId(), userVO.getUsername(), userVO.getPassword(), role);
    }
}
