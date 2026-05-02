package com.kikesoft.moviesapi.entity;

import java.io.Serializable;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Persistence entity that represents a user record stored in the database.
 *
 * @author Enrique Sanchez
 */
@Entity
@Table(name = "users")
public class UserEntity implements Serializable, Persistable<Long> {

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 1746057600000L;

    /**
     * Database identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * User password stored in plain text.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Entity new-state flag used by Spring Data persistence semantics.
     * This field is not persisted to the database.
     */
    @Transient
    private boolean isNew = false;

    /**
     * Creates an empty user entity.
     */
    public UserEntity() {
    }

    /**
     * Creates a user entity with all fields.
     *
     * @param id       user identifier
     * @param username unique username
     * @param password user password
     */
    public UserEntity(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the user identifier.
     *
     * @return user id
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Indicates whether this entity should be treated as new by Spring Data.
     *
     * @return {@code true} when the entity is new; otherwise {@code false}
     */
    @Override
    public boolean isNew() {
        return isNew;
    }

    /**
     * Updates the new-state flag used by Spring Data.
     *
     * @param isNew {@code true} when the entity should be treated as new
     */
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * Marks the entity as not new after it is persisted or loaded.
     */
    @PostPersist
    @PostLoad
    public void markNotNew() {
        this.isNew = false;
    }

    /**
     * Sets the user identifier.
     *
     * @param id user id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username unique username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password user password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
