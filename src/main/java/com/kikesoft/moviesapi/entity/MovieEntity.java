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

@Entity
@Table(name = "movies")
public class MovieEntity implements Serializable {

    private static final long serialVersionUID = 1739356800000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "launch_date")
    private LocalDate launchDate;

    private Integer duration;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Column(length = 1000)
    private String description;

    public MovieEntity() {
    }

    public MovieEntity(Long id, String name, LocalDate launchDate, Integer duration, Rating rating, String description) {
        this.id = id;
        this.name = name;
        this.launchDate = launchDate;
        this.duration = duration;
        this.rating = rating;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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
