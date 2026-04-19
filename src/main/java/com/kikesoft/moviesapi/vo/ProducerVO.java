package com.kikesoft.moviesapi.vo;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

/**
 * Value object used by API endpoints to expose producer data.
 *
 * @author Enrique Sanchez
 */
public class ProducerVO implements Serializable {

    private static final long serialVersionUID = 1745020800000L;

    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    private String profile;

    /**
     * Creates an empty producer value object.
     */
    public ProducerVO() {
    }

    /**
     * Creates a producer value object with all fields.
     *
     * @param id producer identifier
     * @param name producer name
     * @param profile producer profile
     */
    public ProducerVO(Long id, String name, String profile) {
        this.id = id;
        this.name = name;
        this.profile = profile;
    }

    /**
     * Returns the producer identifier.
     *
     * @return producer id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the producer identifier.
     *
     * @param id producer id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the producer name.
     *
     * @return producer name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the producer name.
     *
     * @param name producer name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the producer profile.
     *
     * @return producer profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the producer profile.
     *
     * @param profile producer profile
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * Compares this value object with another object.
     *
     * @param o object to compare
     * @return {@code true} when both objects have the same state
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProducerVO that = (ProducerVO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return profile != null ? profile.equals(that.profile) : that.profile == null;
    }

    /**
     * Computes a hash code using all fields.
     *
     * @return hash code value
     */
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (profile != null ? profile.hashCode() : 0);
        return result;
    }

    /**
     * Returns a printable representation of this value object.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "ProducerVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }
}