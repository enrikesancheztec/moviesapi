package com.kikesoft.moviesapi.vo;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

/**
 * Value object used by the login endpoint to receive credentials.
 */
public class CredentialsVO implements Serializable {

    private static final long serialVersionUID = 1746057600003L;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;

    public CredentialsVO() {
    }

    public CredentialsVO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
