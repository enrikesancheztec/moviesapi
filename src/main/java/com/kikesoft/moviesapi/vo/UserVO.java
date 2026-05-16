package com.kikesoft.moviesapi.vo;

import java.io.Serializable;

import com.kikesoft.moviesapi.entity.Role;

import jakarta.validation.constraints.NotBlank;

/**
 * Value object used by API endpoints to receive and expose user data.
 * The {@code password} field is only populated on inbound requests;
 * it is always {@code null} in API responses.
 *
 * @author Enrique Sanchez
 */
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1746057600001L;

    private Long id;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;

    private Role role = Role.USER;

    /**
     * Creates an empty user value object.
     */
    public UserVO() {
    }

    /**
     * Creates a user value object with all fields.
     *
     * @param id       user identifier
     * @param username unique username
     * @param password user password (set to {@code null} in responses)
     */
    public UserVO(Long id, String username, String password) {
        this(id, username, password, Role.USER);
    }

    /**
     * Creates a user value object with all fields.
     *
     * @param id       user identifier
     * @param username unique username
     * @param password user password (set to {@code null} in responses)
     * @param role     user role
     */
    public UserVO(Long id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role != null ? role : Role.USER;
    }

    /**
     * Returns the user identifier.
     *
     * @return user id
     */
    public Long getId() {
        return id;
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
     * This field is populated only when receiving a request body.
     *
     * @return password or {@code null} in responses
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

    /**
     * Returns the user role.
     *
     * @return role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the user role.
     *
     * @param role user role
     */
    public void setRole(Role role) {
        this.role = role != null ? role : Role.USER;
    }
}
