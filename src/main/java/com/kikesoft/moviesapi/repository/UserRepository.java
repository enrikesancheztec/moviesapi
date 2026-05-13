package com.kikesoft.moviesapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kikesoft.moviesapi.entity.UserEntity;

/**
 * Repository for CRUD operations over {@link UserEntity}.
 *
 * @author Enrique Sanchez
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Finds a user by username.
     *
     * @param username the username to look up
     * @return an {@link Optional} containing the matching entity, or empty if not found
     */
    Optional<UserEntity> findByUsername(String username);
}
