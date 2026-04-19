package com.kikesoft.moviesapi.dao;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kikesoft.moviesapi.entity.ProducerEntity;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.repository.ProducerRepository;
import com.kikesoft.moviesapi.vo.ProducerVO;

@ExtendWith(MockitoExtension.class)
class ProducersDAOTests {

    @Mock
    private ProducerRepository producerRepository;

    @InjectMocks
    private ProducersDAO producersDAO;

    @Test
    void findById_whenProducerExists_returnsMappedProducer() {
        ProducerEntity entity = buildEntity(1L, "John Smith", "Award-winning producer.");

        when(producerRepository.findById(1L)).thenReturn(Optional.of(entity));

        ProducerVO result = producersDAO.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("John Smith", result.getName());
        assertEquals("Award-winning producer.", result.getProfile());
        verify(producerRepository).findById(1L);
    }

    @Test
    void findById_whenProducerDoesNotExist_throwsItemNotFoundException() {
        Long producerId = 99L;

        when(producerRepository.findById(producerId)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> producersDAO.findById(producerId));

        assertEquals("Producer with id 99 not found", exception.getMessage());
        verify(producerRepository).findById(producerId);
    }

    @Test
    void findAll_returnsAllMappedProducers() {
        when(producerRepository.findAll()).thenReturn(List.of(
                buildEntity(1L, "John Smith", "Award-winning producer."),
                buildEntity(2L, "Jane Doe", "Independent producer.")));

        List<ProducerVO> result = producersDAO.findAll();

        assertEquals(2, result.size());
        assertEquals("John Smith", result.get(0).getName());
        assertEquals("Jane Doe", result.get(1).getName());
        verify(producerRepository).findAll();
    }

    @Test
    void findByName_whenProducerExists_returnsMappedProducer() {
        ProducerEntity entity = buildEntity(3L, "John Smith", "Award-winning producer.");

        when(producerRepository.findByName("John Smith")).thenReturn(Optional.of(entity));

        ProducerVO result = producersDAO.findByName("John Smith");

        assertEquals(3L, result.getId());
        assertEquals("John Smith", result.getName());
        verify(producerRepository).findByName("John Smith");
    }

    @Test
    void findByName_whenProducerDoesNotExist_throwsItemNotFoundException() {
        when(producerRepository.findByName("John Smith")).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> producersDAO.findByName("John Smith"));

        assertEquals("Producer with name 'John Smith' not found", exception.getMessage());
        verify(producerRepository).findByName("John Smith");
    }

    @Test
    void add_whenProducerIsNull_returnsNull() {
        ProducerVO result = producersDAO.add(null);

        assertNull(result);
        verify(producerRepository, never()).save(any(ProducerEntity.class));
    }

    @Test
    void add_marksEntityAsNewBeforeSaving() {
        ProducerEntity savedEntity = buildEntity(3L, "John Smith", "Award-winning producer.");
        ProducerVO producerToAdd = new ProducerVO(null, "John Smith", "Award-winning producer.");

        when(producerRepository.save(argThat(entity -> entity != null
                && entity.isNew()
                && entity.getId() == null
                && "John Smith".equals(entity.getName())))).thenReturn(savedEntity);

        ProducerVO result = producersDAO.add(producerToAdd);

        assertEquals(3L, result.getId());
        assertEquals("John Smith", result.getName());
        verify(producerRepository).save(argThat(entity -> entity != null
                && entity.isNew()
                && entity.getId() == null
                && "John Smith".equals(entity.getName())));
    }

    @Test
    void update_whenProducerIsNull_returnsNull() {
        ProducerVO result = producersDAO.update(null);

        assertNull(result);
        verify(producerRepository, never()).save(any(ProducerEntity.class));
    }

    @Test
    void update_savesExistingEntityWithoutMarkingItAsNew() {
        ProducerEntity updatedEntity = buildEntity(3L, "John Smith", "Updated profile.");
        ProducerVO producerToUpdate = new ProducerVO(3L, "John Smith", "Updated profile.");

        when(producerRepository.save(argThat(entity -> entity != null
                && !entity.isNew()
                && Long.valueOf(3L).equals(entity.getId())
                && "Updated profile.".equals(entity.getProfile())))).thenReturn(updatedEntity);

        ProducerVO result = producersDAO.update(producerToUpdate);

        assertEquals(3L, result.getId());
        assertEquals("Updated profile.", result.getProfile());
        verify(producerRepository).save(argThat(entity -> entity != null
                && !entity.isNew()
                && Long.valueOf(3L).equals(entity.getId())
                && "Updated profile.".equals(entity.getProfile())));
    }

    @Test
    void deleteById_delegatesToRepository() {
        producersDAO.deleteById(5L);

        verify(producerRepository).deleteById(5L);
    }

    private ProducerEntity buildEntity(Long id, String name, String profile) {
        return new ProducerEntity(id, name, profile);
    }
}