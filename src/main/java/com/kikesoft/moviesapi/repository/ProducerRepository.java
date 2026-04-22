package com.kikesoft.moviesapi.repository;

import java.util.Optional;

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
	/**
	 * Finds a producer by name.
	 *
	 * @param name producer name
	 * @return optional containing the matching producer entity when found
	 */
	Optional<ProducerEntity> findByName(String name);
}