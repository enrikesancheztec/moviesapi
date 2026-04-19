package com.kikesoft.moviesapi.service;

import java.time.LocalDate;

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

import com.kikesoft.moviesapi.dao.MoviesDAO;
import com.kikesoft.moviesapi.enumeration.Rating;
import com.kikesoft.moviesapi.exception.DuplicatedItemException;
import com.kikesoft.moviesapi.exception.ItemIdMismatchException;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.vo.MovieVO;

@ExtendWith(MockitoExtension.class)
class MoviesServiceTests {

    @Mock
    private MoviesDAO moviesDAO;

    @InjectMocks
    private MoviesService moviesService;

    @Test
    void add_whenMovieDoesNotExist_callsDaoAdd() {
        MovieVO newMovie = new MovieVO(
                null,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");
        MovieVO savedMovie = new MovieVO(
                3L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");

        when(moviesDAO.findByNameAndLaunchDate("Inception", LocalDate.of(2010, 7, 16)))
                .thenThrow(new ItemNotFoundException("Movie not found"));
        when(moviesDAO.add(newMovie)).thenReturn(savedMovie);

        MovieVO result = moviesService.add(newMovie);

        assertEquals(savedMovie, result);
        verify(moviesDAO).add(newMovie);
        verify(moviesDAO, never()).update(org.mockito.ArgumentMatchers.any(MovieVO.class));
    }

    @Test
    void add_whenMovieAlreadyExists_throwsDuplicatedItemException() {
        MovieVO newMovie = new MovieVO(
                null,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");

        when(moviesDAO.findByNameAndLaunchDate("Inception", LocalDate.of(2010, 7, 16)))
                .thenReturn(new MovieVO(
                        3L,
                        "Inception",
                        LocalDate.of(2010, 7, 16),
                        148,
                        Rating.PG_13,
                        "A thief enters dreams to steal corporate secrets."));

        DuplicatedItemException exception = assertThrows(DuplicatedItemException.class,
                () -> moviesService.add(newMovie));

        assertEquals("Movie with name Inception and launch date 2010-07-16 already exists", exception.getMessage());

        verify(moviesDAO, never()).add(org.mockito.ArgumentMatchers.any(MovieVO.class));
        verify(moviesDAO, never()).update(org.mockito.ArgumentMatchers.any(MovieVO.class));
    }

    @Test
    void update_whenMovieIsValid_callsDaoUpdateWithPathId() {
        MovieVO requestMovie = new MovieVO(
                null,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");
        MovieVO existingMovie = new MovieVO(
                3L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");

        when(moviesDAO.findById(3L)).thenReturn(existingMovie);
        when(moviesDAO.findByNameAndLaunchDate("Inception", LocalDate.of(2010, 7, 16)))
                .thenThrow(new ItemNotFoundException("Movie not found"));
        when(moviesDAO.update(argThat(movie -> movie != null && Long.valueOf(3L).equals(movie.getId()))))
                .thenReturn(existingMovie);

        MovieVO result = moviesService.update(3L, requestMovie);

        assertEquals(existingMovie, result);
        verify(moviesDAO).findById(3L);
        verify(moviesDAO).update(argThat(movie -> movie != null && Long.valueOf(3L).equals(movie.getId())));
        verify(moviesDAO, never()).add(org.mockito.ArgumentMatchers.any(MovieVO.class));
    }

    @Test
    void update_whenIdDoesNotMatch_throwsItemIdMismatchException() {
        MovieVO requestMovie = new MovieVO(
                99L,
                "Inception",
                LocalDate.of(2010, 7, 16),
                148,
                Rating.PG_13,
                "A thief enters dreams to steal corporate secrets.");

        ItemIdMismatchException exception = assertThrows(ItemIdMismatchException.class,
                () -> moviesService.update(3L, requestMovie));

        assertEquals("Path id 3 does not match request body id 99", exception.getMessage());

        verify(moviesDAO, never()).findById(3L);
        verify(moviesDAO, never()).add(org.mockito.ArgumentMatchers.any(MovieVO.class));
        verify(moviesDAO, never()).update(org.mockito.ArgumentMatchers.any(MovieVO.class));
    }
}