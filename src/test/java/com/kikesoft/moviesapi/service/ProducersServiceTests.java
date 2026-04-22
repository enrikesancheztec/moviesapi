package com.kikesoft.moviesapi.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kikesoft.moviesapi.dao.ProducersDAO;
import com.kikesoft.moviesapi.exception.DuplicatedItemException;
import com.kikesoft.moviesapi.exception.ItemIdMismatchException;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.vo.ProducerVO;

@ExtendWith(MockitoExtension.class)
class ProducersServiceTests {

    @Mock
    private ProducersDAO producersDAO;

    @InjectMocks
    private ProducersService producersService;

    @Test
    void findById_delegatesToDao() {
        ProducerVO producer = new ProducerVO(1L, "John Smith", "Award-winning producer.");

        when(producersDAO.findById(1L)).thenReturn(producer);

        ProducerVO result = producersService.findById(1L);

        assertEquals(producer, result);
        verify(producersDAO).findById(1L);
    }

    @Test
    void findAll_delegatesToDao() {
        List<ProducerVO> producers = List.of(new ProducerVO(1L, "John Smith", "Award-winning producer."));

        when(producersDAO.findAll()).thenReturn(producers);

        List<ProducerVO> result = producersService.findAll();

        assertEquals(producers, result);
        verify(producersDAO).findAll();
    }

    @Test
    void add_whenProducerDoesNotExist_callsDaoAdd() {
        ProducerVO newProducer = new ProducerVO(null, "John Smith", "Award-winning producer.");
        ProducerVO savedProducer = new ProducerVO(3L, "John Smith", "Award-winning producer.");

        when(producersDAO.findByName("John Smith")).thenThrow(new ItemNotFoundException("Producer not found"));
        when(producersDAO.add(newProducer)).thenReturn(savedProducer);

        ProducerVO result = producersService.add(newProducer);

        assertEquals(savedProducer, result);
        verify(producersDAO).add(newProducer);
        verify(producersDAO, never()).update(org.mockito.ArgumentMatchers.any(ProducerVO.class));
    }

    @Test
    void add_whenProducerAlreadyExists_throwsDuplicatedItemException() {
        ProducerVO newProducer = new ProducerVO(null, "John Smith", "Award-winning producer.");

        when(producersDAO.findByName("John Smith")).thenReturn(new ProducerVO(3L, "John Smith", "Award-winning producer."));

        DuplicatedItemException exception = assertThrows(DuplicatedItemException.class,
                () -> producersService.add(newProducer));

        assertEquals("Producer with name John Smith already exists", exception.getMessage());
        verify(producersDAO, never()).add(org.mockito.ArgumentMatchers.any(ProducerVO.class));
    }

    @Test
    void update_whenProducerIsValid_callsDaoUpdateWithPathId() {
        ProducerVO requestProducer = new ProducerVO(null, "John Smith", "Award-winning producer.");
        ProducerVO existingProducer = new ProducerVO(3L, "John Smith", "Award-winning producer.");

        when(producersDAO.findById(3L)).thenReturn(existingProducer);
        when(producersDAO.findByName("John Smith")).thenThrow(new ItemNotFoundException("Producer not found"));
        when(producersDAO.update(argThat(producer -> producer != null && Long.valueOf(3L).equals(producer.getId()))))
                .thenReturn(existingProducer);

        ProducerVO result = producersService.update(3L, requestProducer);

        assertEquals(existingProducer, result);
        verify(producersDAO).findById(3L);
        verify(producersDAO).update(argThat(producer -> producer != null && Long.valueOf(3L).equals(producer.getId())));
    }

    @Test
    void update_whenIdDoesNotMatch_throwsItemIdMismatchException() {
        ProducerVO requestProducer = new ProducerVO(99L, "John Smith", "Award-winning producer.");

        ItemIdMismatchException exception = assertThrows(ItemIdMismatchException.class,
                () -> producersService.update(3L, requestProducer));

        assertEquals("Path id 3 does not match request body id 99", exception.getMessage());
        verify(producersDAO, never()).findById(3L);
        verify(producersDAO, never()).update(org.mockito.ArgumentMatchers.any(ProducerVO.class));
    }

    @Test
    void update_whenDuplicateExistsWithDifferentId_throwsDuplicatedItemException() {
        ProducerVO requestProducer = new ProducerVO(null, "John Smith", "Award-winning producer.");

        when(producersDAO.findById(3L)).thenReturn(new ProducerVO(3L, "John Smith", "Award-winning producer."));
        when(producersDAO.findByName("John Smith")).thenReturn(new ProducerVO(7L, "John Smith", "Award-winning producer."));

        DuplicatedItemException exception = assertThrows(DuplicatedItemException.class,
                () -> producersService.update(3L, requestProducer));

        assertEquals("Producer with name John Smith already exists with id 7", exception.getMessage());
        verify(producersDAO, never()).update(org.mockito.ArgumentMatchers.any(ProducerVO.class));
    }

    @Test
    void update_whenDuplicateExistsWithSameId_updatesProducer() {
        ProducerVO requestProducer = new ProducerVO(null, "John Smith", "Award-winning producer.");
        ProducerVO updatedProducer = new ProducerVO(3L, "John Smith", "Award-winning producer.");

        when(producersDAO.findById(3L)).thenReturn(updatedProducer);
        when(producersDAO.findByName("John Smith")).thenReturn(updatedProducer);
        when(producersDAO.update(argThat(producer -> producer != null && Long.valueOf(3L).equals(producer.getId()))))
                .thenReturn(updatedProducer);

        ProducerVO result = producersService.update(3L, requestProducer);

        assertEquals(updatedProducer, result);
        verify(producersDAO).update(argThat(producer -> producer != null && Long.valueOf(3L).equals(producer.getId())));
    }

    @Test
    void update_whenProducerDoesNotExist_throwsItemNotFoundException() {
        ProducerVO requestProducer = new ProducerVO(null, "John Smith", "Award-winning producer.");

        when(producersDAO.findById(3L)).thenThrow(new ItemNotFoundException("Producer with id 3 not found"));

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> producersService.update(3L, requestProducer));

        assertEquals("Producer with id 3 not found", exception.getMessage());
        verify(producersDAO, never()).update(org.mockito.ArgumentMatchers.any(ProducerVO.class));
    }

    @Test
    void update_whenBodyIdMatchesPathId_updatesProducer() {
        ProducerVO requestProducer = new ProducerVO(3L, "John Smith", "Award-winning producer.");
        ProducerVO updatedProducer = new ProducerVO(3L, "John Smith", "Award-winning producer.");

        when(producersDAO.findById(3L)).thenReturn(updatedProducer);
        when(producersDAO.findByName("John Smith")).thenThrow(new ItemNotFoundException("Producer not found"));
        when(producersDAO.update(argThat(producer -> producer != null && Long.valueOf(3L).equals(producer.getId()))))
                .thenReturn(updatedProducer);

        ProducerVO result = producersService.update(3L, requestProducer);

        assertEquals(updatedProducer, result);
        verify(producersDAO).update(argThat(producer -> producer != null && Long.valueOf(3L).equals(producer.getId())));
    }

    @Test
    void update_whenFoundDuplicateHasNullId_updatesProducer() {
        ProducerVO requestProducer = new ProducerVO(null, "John Smith", "Award-winning producer.");
        ProducerVO foundProducerWithoutId = new ProducerVO(null, "John Smith", "Award-winning producer.");
        ProducerVO updatedProducer = new ProducerVO(3L, "John Smith", "Award-winning producer.");

        when(producersDAO.findById(3L)).thenReturn(updatedProducer);
        when(producersDAO.findByName("John Smith")).thenReturn(foundProducerWithoutId);
        when(producersDAO.update(argThat(producer -> producer != null && Long.valueOf(3L).equals(producer.getId()))))
                .thenReturn(updatedProducer);

        ProducerVO result = producersService.update(3L, requestProducer);

        assertEquals(updatedProducer, result);
        verify(producersDAO).update(argThat(producer -> producer != null && Long.valueOf(3L).equals(producer.getId())));
    }
}