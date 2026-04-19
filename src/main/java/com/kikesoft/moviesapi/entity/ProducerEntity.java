package com.kikesoft.moviesapi.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Persistence entity that represents a producer record stored in the database.
 *
 * @author Enrique Sanchez
 */
@Entity
@Table(name = "producers")
public class ProducerEntity implements Serializable, Persistable<Long> {

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 1745020800001L;

    /**
     * Database identifier for the producer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Producer name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Optional producer profile.
     */
    @Column(length = 1000)
    private String profile;

    /**
     * Movies associated with this producer. Loaded lazily; not exposed via the API.
     */
    @OneToMany(mappedBy = "producer", fetch = FetchType.LAZY)
    private List<MovieEntity> movies = new ArrayList<>();

    /**
     * Entity new-state flag used by Spring Data persistence semantics.
     * This field is not persisted to the database.
     */
    @Transient
    private boolean isNew = false;

    /**
     * Creates an empty producer entity.
     */
    public ProducerEntity() {
    }

    /**
     * Creates a producer entity with all supported fields.
     *
     * @param id producer identifier
     * @param name producer name
     * @param profile producer profile
     */
    public ProducerEntity(Long id, String name, String profile) {
        this.id = id;
        this.name = name;
        this.profile = profile;
    }

    /**
     * Returns the producer identifier.
     *
     * @return producer id
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
     * Compares this entity with another object.
     *
     * @param o object to compare
     * @return {@code true} when both represent the same state
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProducerEntity that = (ProducerEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return profile != null ? profile.equals(that.profile) : that.profile == null;
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
        result = 31 * result + (profile != null ? profile.hashCode() : 0);
        return result;
    }

    /**
     * Returns a printable representation of the entity.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "ProducerEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }
}