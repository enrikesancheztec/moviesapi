package com.kikesoft.moviesapi.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import com.kikesoft.moviesapi.entity.MovieEntity;
import com.kikesoft.moviesapi.entity.ProducerEntity;
import com.kikesoft.moviesapi.enumeration.Rating;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.repository.MovieRepository;
import com.kikesoft.moviesapi.repository.ProducerRepository;
import com.kikesoft.moviesapi.vo.MovieVO;

@ExtendWith(MockitoExtension.class)
class MoviesDAOTests {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ProducerRepository producerRepository;

    @InjectMocks
    private MoviesDAO moviesDAO;

    @Test
    void findById_whenMovieExists_returnsMappedMovie() {
        MovieEntity entity = buildEntity(
                1L,
                "Star Wars: Episode IV - A New Hope",
                LocalDate.of(1977, 5, 25),
                121,
                Rating.PG,
                "Luke Skywalker begins his journey as a Jedi Knight...");

        when(movieRepository.findById(1L)).thenReturn(Optional.of(entity));

        MovieVO result = moviesDAO.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Star Wars: Episode IV - A New Hope", result.getName());
        assertEquals(LocalDate.of(1977, 5, 25), result.getLaunchDate());
        assertEquals(121, result.getDuration());
        assertEquals(Rating.PG, result.getRating());
        verify(movieRepository).findById(1L);
    }

    @Test
    void findById_whenMovieDoesNotExist_throwsItemNotFoundException() {
        Long movieId = 99L;

        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () -> moviesDAO.findById(movieId));

        assertEquals("Movie with id 99 not found", exception.getMessage());
        verify(movieRepository).findById(movieId);
    }

    @Test
    void findAll_returnsAllMappedMovies() {
        when(movieRepository.findAll()).thenReturn(List.of(
                buildEntity(
                        1L,
                        "Star Wars: Episode IV - A New Hope",
                        LocalDate.of(1977, 5, 25),
                        121,
                        Rating.PG,
                        "Luke Skywalker begins his journey as a Jedi Knight..."),
                buildEntity(
                        2L,
                        "The Godfather",
                        LocalDate.of(1972, 3, 24),
                        175,
                        Rating.R,
                        "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son...")));

        List<MovieVO> result = moviesDAO.findAll();

        assertEquals(2, result.size());
        assertEquals("Star Wars: Episode IV - A New Hope", result.get(0).getName());
        assertEquals("The Godfather", result.get(1).getName());
        verify(movieRepository).findAll();
    }

    @Test
    void findByNameAndLaunchDate_whenMovieExists_returnsMappedMovie() {
        MovieEntity entity = buildEntity(
                3L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");

        when(movieRepository.findByNameAndLaunchDate("Inception", LocalDate.of(2010, 7, 16)))
                .thenReturn(Optional.of(entity));

        MovieVO result = moviesDAO.findByNameAndLaunchDate("Inception", LocalDate.of(2010, 7, 16));

        assertEquals(3L, result.getId());
        assertEquals("Inception", result.getName());
        verify(movieRepository).findByNameAndLaunchDate("Inception", LocalDate.of(2010, 7, 16));
    }

    @Test
    void findByNameAndLaunchDate_whenMovieDoesNotExist_throwsItemNotFoundException() {
        String movieName = "Inception";
        LocalDate launchDate = LocalDate.of(2010, 7, 16);

        when(movieRepository.findByNameAndLaunchDate(movieName, launchDate))
                .thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> moviesDAO.findByNameAndLaunchDate(movieName, launchDate));

        assertEquals("Movie with name 'Inception' and launch date 2010-07-16 not found", exception.getMessage());
        verify(movieRepository).findByNameAndLaunchDate(movieName, launchDate);
    }

    @Test
    void add_whenMovieIsNull_returnsNull() {
        MovieVO result = moviesDAO.add(null);

        assertNull(result);
        verify(movieRepository, never()).save(org.mockito.ArgumentMatchers.any(MovieEntity.class));
    }

    @Test
    void add_marksEntityAsNewBeforeSaving() {
        MovieEntity savedEntity = buildEntity(
                3L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");
        MovieVO movieToAdd = new MovieVO(
                null,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");

        when(movieRepository.save(argThat(entity -> entity != null
                && entity.isNew()
                && entity.getId() == null
                && "Inception".equals(entity.getName()))))
                .thenReturn(savedEntity);

        MovieVO result = moviesDAO.add(movieToAdd);

        assertEquals(3L, result.getId());
        assertEquals("Inception", result.getName());
        verify(movieRepository).save(argThat(entity -> entity != null
                && entity.isNew()
                && entity.getId() == null
                && "Inception".equals(entity.getName())));
    }

    @Test
    void add_withProducerId_resolvesProducerAndPersistsAssociation() {
        ProducerEntity producer = buildProducer(10L, "John Smith", "Award-winning producer.");
        MovieEntity savedEntity = buildEntity(
                3L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");
        savedEntity.setProducer(producer);

        MovieVO movieToAdd = new MovieVO(
                null,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");
        movieToAdd.setProducerId(10L);

        when(producerRepository.findById(10L)).thenReturn(Optional.of(producer));
        when(movieRepository.save(argThat(entity -> entity != null
                && entity.isNew()
                && entity.getProducer() != null
                && Long.valueOf(10L).equals(entity.getProducer().getId()))))
                .thenReturn(savedEntity);

        MovieVO result = moviesDAO.add(movieToAdd);

        assertEquals(10L, result.getProducerId());
        assertEquals("John Smith", result.getProducer().getName());
        verify(producerRepository).findById(10L);
    }

    @Test
    void add_withUnknownProducerId_throwsItemNotFoundException() {
        MovieVO movieToAdd = new MovieVO(
                null,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");
        movieToAdd.setProducerId(99L);

        when(producerRepository.findById(99L)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class, () -> moviesDAO.add(movieToAdd));

        assertEquals("Producer with id 99 not found", exception.getMessage());
        verify(movieRepository, never()).save(org.mockito.ArgumentMatchers.any(MovieEntity.class));
    }

    @Test
    void update_whenMovieIsNull_returnsNull() {
        MovieVO result = moviesDAO.update(null);

        assertNull(result);
        verify(movieRepository, never()).save(org.mockito.ArgumentMatchers.any(MovieEntity.class));
    }

    @Test
    void update_savesExistingEntityWithoutMarkingItAsNew() {
        MovieEntity updatedEntity = buildEntity(
                3L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                150,
                Rating.PG_13,
                "Updated description.");
        MovieVO movieToUpdate = new MovieVO(
                3L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                150,
                Rating.PG_13,
                "Updated description.");

        when(movieRepository.save(argThat(entity -> entity != null
                && !entity.isNew()
                && Long.valueOf(3L).equals(entity.getId())
                && entity.getDuration() == 150)))
                .thenReturn(updatedEntity);

        MovieVO result = moviesDAO.update(movieToUpdate);

        assertEquals(3L, result.getId());
        assertEquals(150, result.getDuration());
        assertEquals("Updated description.", result.getDescription());
        verify(movieRepository).save(argThat(entity -> entity != null
                && !entity.isNew()
                && Long.valueOf(3L).equals(entity.getId())
                && entity.getDuration() == 150));
    }

    @Test
    void update_withProducerId_resolvesProducerAndPersistsAssociation() {
        ProducerEntity producer = buildProducer(10L, "John Smith", "Award-winning producer.");
        MovieEntity updatedEntity = buildEntity(
                3L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                150,
                Rating.PG_13,
                "Updated description.");
        updatedEntity.setProducer(producer);

        MovieVO movieToUpdate = new MovieVO(
                3L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                150,
                Rating.PG_13,
                "Updated description.");
        movieToUpdate.setProducerId(10L);

        when(producerRepository.findById(10L)).thenReturn(Optional.of(producer));
        when(movieRepository.save(argThat(entity -> entity != null
                && Long.valueOf(3L).equals(entity.getId())
                && entity.getProducer() != null
                && Long.valueOf(10L).equals(entity.getProducer().getId()))))
                .thenReturn(updatedEntity);

        MovieVO result = moviesDAO.update(movieToUpdate);

        assertEquals(10L, result.getProducerId());
        assertEquals("John Smith", result.getProducer().getName());
        verify(producerRepository).findById(10L);
    }

    @Test
    void update_withoutProducerId_preservesExistingProducer() {
        ProducerEntity producer = buildProducer(10L, "John Smith", "Award-winning producer.");
        MovieEntity existingEntity = buildEntity(
                3L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");
        existingEntity.setProducer(producer);

        MovieEntity updatedEntity = buildEntity(
                3L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                150,
                Rating.PG_13,
                "Updated description.");
        updatedEntity.setProducer(producer);

        MovieVO movieToUpdate = new MovieVO(
                3L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                150,
                Rating.PG_13,
                "Updated description.");

        when(movieRepository.findById(3L)).thenReturn(Optional.of(existingEntity));
        when(movieRepository.save(argThat(entity -> entity != null
                && entity.getProducer() != null
                && Long.valueOf(10L).equals(entity.getProducer().getId()))))
                .thenReturn(updatedEntity);

        MovieVO result = moviesDAO.update(movieToUpdate);

        assertEquals(10L, result.getProducerId());
        assertEquals("John Smith", result.getProducer().getName());
        verify(movieRepository).findById(3L);
    }

    @Test
    void deleteById_delegatesToRepository() {
        moviesDAO.deleteById(5L);

        verify(movieRepository).deleteById(5L);
    }

    private MovieEntity buildEntity(
            Long id,
            String name,
            LocalDate launchDate,
            Integer duration,
            Rating rating,
            String description) {
        return new MovieEntity(id, name, launchDate, duration, rating, description);
    }

    private ProducerEntity buildProducer(Long id, String name, String profile) {
        return new ProducerEntity(id, name, profile);
    }
}
