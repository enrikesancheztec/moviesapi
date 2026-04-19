package com.kikesoft.moviesapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kikesoft.moviesapi.entity.ProducerEntity;

/**
 * Repository for CRUD operations over {@link ProducerEntity}.
 *
 * @author Enrique Sanchez
 */
@Repository
public interface ProducerRepository extends JpaRepository<ProducerEntity, Long> {
}