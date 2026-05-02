package com.kikesoft.moviesapi.dao;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kikesoft.moviesapi.entity.UserEntity;
import com.kikesoft.moviesapi.exception.DuplicatedItemException;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.mapper.UserMapper;
import com.kikesoft.moviesapi.repository.UserRepository;
import com.kikesoft.moviesapi.vo.UserVO;

/**
 * Data access component that coordinates user persistence operations.
 *
 * @author Enrique Sanchez
 */
@Repository
public class UsersDAO {
    private static final Logger LOGGER = LogManager.getLogger(UsersDAO.class);

    /**
     * Repository dependency used for user persistence operations.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all users currently stored.
     *
     * @return list of users (password is {@code null} in every item)
     */
    public List<UserVO> findAll() {
        LOGGER.debug("DAO findAll - fetching all users");
        List<UserVO> users = userRepository.findAll().stream().map(UserMapper::toVO).toList();
        LOGGER.debug("DAO findAll - retrieved {} users", users.size());
        return users;
    }

    /**
     * Finds a user by id.
     *
     * @param id user identifier
     * @return user representation (password is {@code null})
     * @throws ItemNotFoundException when no user exists with the given id
     */
    public UserVO findById(Long id) {
        LOGGER.debug("DAO findById - id={}", id);
        Optional<UserVO> userVO = userRepository.findById(id).map(UserMapper::toVO);

        if (userVO.isEmpty()) {
            LOGGER.warn("DAO findById - user not found for id={}", id);
            throw new ItemNotFoundException("User with id " + id + " not found");
        }

        LOGGER.debug("DAO findById - user found for id={}", id);
        return userVO.get();
    }

    /**
     * Persists a new user and returns the stored representation.
     * The password is never included in the returned value object.
     *
     * @param userVO user to persist (must contain a non-blank username and password)
     * @return persisted user with {@code password} set to {@code null}
     * @throws DuplicatedItemException when another user already uses the same username
     */
    public UserVO add(UserVO userVO) {
        LOGGER.debug("DAO add - checking for duplicate username='{}'", userVO != null ? userVO.getUsername() : null);

        if (userVO == null) {
            LOGGER.warn("DAO add - input user is null, nothing to persist");
            return null;
        }

        Optional<UserEntity> existing = userRepository.findByUsername(userVO.getUsername());
        if (existing.isPresent()) {
            LOGGER.warn("DAO add - username '{}' already exists", userVO.getUsername());
            throw new DuplicatedItemException("User with username '" + userVO.getUsername() + "' already exists");
        }

        UserEntity userEntity = UserMapper.toEntity(userVO);
        userEntity.setNew(true);
        UserVO saved = UserMapper.toVO(userRepository.save(userEntity));
        LOGGER.debug("DAO add - user persisted with id={}", saved != null ? saved.getId() : null);
        return saved;
    }
}
