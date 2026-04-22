package com.kikesoft.moviesapi.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kikesoft.moviesapi.dao.ProducersDAO;
import com.kikesoft.moviesapi.exception.DuplicatedItemException;
import com.kikesoft.moviesapi.exception.ItemIdMismatchException;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.vo.ProducerVO;

/**
 * Service layer that exposes producer read operations and delegates persistence
 * access to the DAO.
 *
 * @author Enrique Sanchez
 */
@Service
public class ProducersService {
    private static final Logger LOGGER = LogManager.getLogger(ProducersService.class);

    /**
     * DAO dependency used to access producer data.
     */
    @Autowired
    private ProducersDAO producersDAO;

    /**
     * Finds a producer by id.
     *
     * @param id producer identifier
     * @return producer representation
     */
    public ProducerVO findById(Long id) {
        LOGGER.debug("Service findById - id={}", id);
        return producersDAO.findById(id);
    }

    /**
     * Retrieves all producers available in the persistence layer.
     *
     * @return list of producers
     */
    public List<ProducerVO> findAll() {
        LOGGER.debug("Service findAll - fetching producers list");
        return producersDAO.findAll();
    }

    /**
     * Adds a new producer to the persistence layer.
     *
     * @param producerVO producer payload to persist
     * @return persisted producer representation or {@code null} when the input is
     * {@code null}
     */
    public ProducerVO add(ProducerVO producerVO) {
        LOGGER.debug("Service add - creating producer with name='{}'", producerVO.getName());
        try {
            producersDAO.findByName(producerVO.getName());
            LOGGER.warn("Service add - duplicated producer detected for name='{}'", producerVO.getName());
            throw new DuplicatedItemException("Producer with name " + producerVO.getName() + " already exists");
        } catch (final ItemNotFoundException infe) {
            LOGGER.debug("Service add - producer not found by name, proceeding with creation");
            return producersDAO.add(producerVO);
        }
    }

    /**
     * Updates an existing producer in the persistence layer.
     *
     * @param id producer identifier to update
     * @param producerVO producer payload with updated data
     * @return updated producer representation or {@code null} when the input is
     * {@code null}
     * @throws ItemNotFoundException when the producer id does not exist
     * @throws ItemIdMismatchException when path id and payload id do not match
     * @throws DuplicatedItemException when another producer already uses the same
     * name
     */
    public ProducerVO update(final Long id, final ProducerVO producerVO) {
        LOGGER.debug("Service update - pathId={}, payloadId={}", id, producerVO.getId());
        if (producerVO.getId() != null && !producerVO.getId().equals(id)) {
            LOGGER.warn("Service update - id mismatch pathId={} payloadId={}", id, producerVO.getId());
            throw new ItemIdMismatchException(
                    "Path id " + id + " does not match request body id " + producerVO.getId());
        }

        LOGGER.debug("Service update - verifying producer exists for id={}", id);
        producersDAO.findById(id);

        try {
            ProducerVO foundProducer = producersDAO.findByName(producerVO.getName());
            if (foundProducer.getId() != null && !foundProducer.getId().equals(id)) {
                LOGGER.warn("Service update - duplicated producer detected for name='{}', existingId={}",
                        producerVO.getName(), foundProducer.getId());
                throw new DuplicatedItemException(
                        "Producer with name " + producerVO.getName() + " already exists with id "
                                + foundProducer.getId());
            }
        } catch (final ItemNotFoundException infe) {
            LOGGER.debug("Service update - no duplicate found by name, proceeding with update");
        }

        producerVO.setId(id);
        LOGGER.debug("Service update - persisting producer id={}", id);
        return producersDAO.update(producerVO);
    }
}