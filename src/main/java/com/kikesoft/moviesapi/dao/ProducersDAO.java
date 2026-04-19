package com.kikesoft.moviesapi.dao;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kikesoft.moviesapi.entity.ProducerEntity;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.mapper.ProducerMapper;
import com.kikesoft.moviesapi.repository.ProducerRepository;
import com.kikesoft.moviesapi.vo.ProducerVO;

/**
 * Data access component that coordinates producer persistence operations.
 *
 * @author Enrique Sanchez
 */
@Repository
public class ProducersDAO {
    private static final Logger LOGGER = LogManager.getLogger(ProducersDAO.class);

    /**
     * Repository dependency used for producer persistence operations.
     */
    @Autowired
    private ProducerRepository producerRepository;

    /**
     * Finds a producer by id.
     *
     * @param id producer identifier
     * @return producer representation
     */
    public ProducerVO findById(Long id) {
        LOGGER.debug("DAO findById - id={}", id);
        Optional<ProducerVO> producerVO = producerRepository.findById(id).map(ProducerMapper::toVO);

        if (producerVO.isEmpty()) {
            LOGGER.warn("DAO findById - producer not found for id={}", id);
            throw new ItemNotFoundException("Producer with id " + id + " not found");
        }

        LOGGER.debug("DAO findById - producer found for id={}", id);
        return producerVO.get();
    }

    /**
     * Retrieves all producers currently stored.
     *
     * @return list of producers
     */
    public List<ProducerVO> findAll() {
        LOGGER.debug("DAO findAll - fetching all producers");
        List<ProducerVO> producers = producerRepository.findAll().stream().map(ProducerMapper::toVO).toList();
        LOGGER.debug("DAO findAll - retrieved {} producers", producers.size());
        return producers;
    }

    /**
     * Finds a producer by name.
     *
     * @param name producer name
     * @return producer representation
     * @throws ItemNotFoundException if no producer is found with the given name
     */
    public ProducerVO findByName(String name) {
        LOGGER.debug("DAO findByName - name='{}'", name);
        Optional<ProducerEntity> producerEntity = producerRepository.findByName(name);

        if (producerEntity.isEmpty()) {
            LOGGER.warn("DAO findByName - producer not found for name='{}'", name);
            throw new ItemNotFoundException("Producer with name '" + name + "' not found");
        }

        LOGGER.debug("DAO findByName - producer found for name='{}'", name);
        return ProducerMapper.toVO(producerEntity.get());
    }

    /**
     * Persists a new producer and returns the stored representation.
     *
     * @param producerVO producer to persist
     * @return persisted producer or {@code null} when the input is {@code null}
     */
    public ProducerVO add(ProducerVO producerVO) {
        LOGGER.debug("DAO add - mapping and persisting new producer");
        ProducerEntity producerEntity = ProducerMapper.toEntity(producerVO);
        if (producerEntity == null) {
            LOGGER.warn("DAO add - input producer is null, nothing to persist");
            return null;
        }
        producerEntity.setNew(true);
        ProducerVO saved = ProducerMapper.toVO(producerRepository.save(producerEntity));
        LOGGER.debug("DAO add - producer persisted with id={}", saved != null ? saved.getId() : null);
        return saved;
    }

    /**
     * Persists updates for an existing producer and returns the stored representation.
     *
     * @param producerVO producer to update
     * @return updated producer or {@code null} when the input is {@code null}
     */
    public ProducerVO update(ProducerVO producerVO) {
        LOGGER.debug("DAO update - mapping and persisting producer update for id={}",
                producerVO != null ? producerVO.getId() : null);
        ProducerEntity producerEntity = ProducerMapper.toEntity(producerVO);
        if (producerEntity == null) {
            LOGGER.warn("DAO update - input producer is null, nothing to persist");
            return null;
        }
        ProducerVO updated = ProducerMapper.toVO(producerRepository.save(producerEntity));
        LOGGER.debug("DAO update - producer updated with id={}", updated != null ? updated.getId() : null);
        return updated;
    }

    /**
     * Deletes a producer by id.
     *
     * @param id producer identifier
     */
    public void deleteById(Long id) {
        LOGGER.debug("DAO deleteById - deleting producer with id={}", id);
        producerRepository.deleteById(id);
    }
}