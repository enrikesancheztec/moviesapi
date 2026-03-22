package com.kikesoft.moviesapi.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.kikesoft.moviesapi.enumeration.Rating;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Persistence entity that represents a movie record stored in the database.
 *
 * @author Enrique Sanchez
 */
@Entity
@Table(name = "movies")
public class MovieEntity implements Serializable {

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 1739356800000L;

    /**
     * Database identifier for the movie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Movie title.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Release date of the movie.
     */
    @Column(name = "launch_date")
    private LocalDate launchDate;

    /**
     * Runtime in minutes.
     */
    private Integer duration;

    /**
     * Age rating classification.
        * Allowed values: {@link Rating#G}, {@link Rating#PG}, {@link Rating#PG_13},
        * {@link Rating#R}, {@link Rating#NC_17}.
     */
    @Enumerated(EnumType.STRING)
    private Rating rating;

    /**
     * Brief movie synopsis.
     */
    @Column(length = 1000)
    private String description;

    /**
     * Creates an empty movie entity.
     */
    public MovieEntity() {
    }

    /**
     * Creates a movie entity with all supported fields.
     *
     * @param id movie identifier
     * @param name movie title
     * @param launchDate release date
     * @param duration runtime in minutes
        * @param rating age rating classification. Allowed values: {@link Rating#G},
        *               {@link Rating#PG}, {@link Rating#PG_13}, {@link Rating#R},
        *               {@link Rating#NC_17}
     * @param description short movie description
     */
    public MovieEntity(Long id, String name, LocalDate launchDate, Integer duration, Rating rating, String description) {
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
     * @return movie name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the movie title.
     *
     * @param name movie name
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
     * @param launchDate movie release date
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
     * Returns the movie rating category.
        * Allowed values: {@link Rating#G}, {@link Rating#PG}, {@link Rating#PG_13},
        * {@link Rating#R}, {@link Rating#NC_17}.
     *
     * @return movie rating
     */
    public Rating getRating() {
        return rating;
    }

    /**
     * Sets the movie rating category.
        * Allowed values: {@link Rating#G}, {@link Rating#PG}, {@link Rating#PG_13},
        * {@link Rating#R}, {@link Rating#NC_17}.
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
     * Compares this entity with another object.
     *
     * @param o object to compare
     * @return {@code true} when both represent the same state
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieEntity that = (MovieEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (launchDate != null ? !launchDate.equals(that.launchDate) : that.launchDate != null) return false;
        if (duration != null ? !duration.equals(that.duration) : that.duration != null) return false;
        if (rating != null ? !rating.equals(that.rating) : that.rating != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;
    }

    /**
     * Computes hash code based on entity fields.
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
     * Returns a printable representation of the entity.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "MovieEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", launchDate=" + launchDate +
                ", duration=" + duration +
                ", rating=" + rating +
                ", description='" + description + '\'' +
                '}';
    }
}
