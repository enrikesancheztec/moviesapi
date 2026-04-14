package com.kikesoft.moviesapi.vo;

import java.io.Serializable;
import java.time.LocalDate;

import com.kikesoft.moviesapi.enumeration.Rating;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Value object used by API endpoints to expose movie data.
 *
 * @author Enrique Sanchez
 */
public class MovieVO implements Serializable {

    private static final long serialVersionUID = 1739356800000L;

    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotNull(message = "Launch date is mandatory")
    private LocalDate launchDate;
    @NotNull(message = "Duration is mandatory")
    private Integer duration;
    private Rating rating;
    private String description;

    /**
     * Creates an empty movie value object.
     */
    public MovieVO() {
    }

    /**
     * Creates a movie value object with all fields.
     *
     * @param id movie identifier
     * @param name movie title
     * @param launchDate release date
     * @param duration runtime in minutes
     * @param rating age rating classification
     * @param description short movie description
     */
    public MovieVO(Long id, String name, LocalDate launchDate, Integer duration, Rating rating, String description) {
        this.id = id;
        this.name = name;
        this.launchDate = launchDate;
        this.duration = duration;
        this.rating = rating;
        this.description = description;
    }

    /**
     * Returns the movie identifier.
     *
     * @return movie id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the movie identifier.
     *
     * @param id movie id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the movie title.
     *
     * @return movie title
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the movie title.
     *
     * @param name movie title
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the movie release date.
     *
     * @return launch date
     */
    public LocalDate getLaunchDate() {
        return launchDate;
    }

    /**
     * Sets the movie release date.
     *
     * @param launchDate release date
     */
    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    /**
     * Returns the movie runtime in minutes.
     *
     * @return duration in minutes
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * Sets the movie runtime in minutes.
     *
     * @param duration duration in minutes
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * Returns the movie age rating classification.
     *
     * @return movie rating
     */
    public Rating getRating() {
        return rating;
    }

    /**
     * Sets the movie age rating classification.
     *
     * @param rating movie rating
     */
    public void setRating(Rating rating) {
        this.rating = rating;
    }

    /**
     * Returns the movie description.
     *
     * @return movie description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the movie description.
     *
     * @param description movie description
     */
    public void setDescription(String description) {
        this.description = description;
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

        MovieVO movieVO = (MovieVO) o;

        if (id != null ? !id.equals(movieVO.id) : movieVO.id != null) return false;
        if (name != null ? !name.equals(movieVO.name) : movieVO.name != null) return false;
        if (launchDate != null ? !launchDate.equals(movieVO.launchDate) : movieVO.launchDate != null) return false;
        if (duration != null ? !duration.equals(movieVO.duration) : movieVO.duration != null) return false;
        if (rating != null ? !rating.equals(movieVO.rating) : movieVO.rating != null) return false;
        return description != null ? description.equals(movieVO.description) : movieVO.description == null;
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
        result = 31 * result + (launchDate != null ? launchDate.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    /**
     * Returns a printable representation of this value object.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "MovieVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", launchDate=" + launchDate +
                ", duration=" + duration +
                ", rating=" + rating +
                ", description='" + description + '\'' +
                '}';
    }

}
