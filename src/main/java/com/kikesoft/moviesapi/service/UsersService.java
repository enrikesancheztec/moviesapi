package com.kikesoft.moviesapi.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kikesoft.moviesapi.dao.UsersDAO;
import com.kikesoft.moviesapi.vo.UserVO;

/**
 * Service layer that exposes user operations and delegates persistence access
 * to the DAO.
 *
 * @author Enrique Sanchez
 */
@Service
public class UsersService {

    private static final Logger LOGGER = LogManager.getLogger(UsersService.class);

    /**
     * DAO dependency used to access user data.
     */
    @Autowired
    private UsersDAO usersDAO;

    /**
     * Encoder dependency used to store user passwords in encoded form.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Retrieves all users available in the persistence layer.
     *
     * @return list of users (password is {@code null} in every item)
     */
    public List<UserVO> findAll() {
        LOGGER.debug("Service findAll - fetching users list");
        return usersDAO.findAll();
    }

    /**
     * Finds a user by id.
     *
     * @param id user identifier
     * @return user representation (password is {@code null})
     */
    public UserVO findById(Long id) {
        LOGGER.debug("Service findById - id={}", id);
        return usersDAO.findById(id);
    }

    /**
     * Adds a new user to the persistence layer.
     *
     * @param userVO user payload to persist
     * @return persisted user representation with {@code password} set to
     * {@code null}
     */
    public UserVO add(UserVO userVO) {
        LOGGER.debug("Service add - creating user with username='{}'", userVO != null ? userVO.getUsername() : null);

        if (userVO != null && userVO.getPassword() != null) {
            userVO.setPassword(passwordEncoder.encode(userVO.getPassword()));
        }

        return usersDAO.add(userVO);
    }
}
