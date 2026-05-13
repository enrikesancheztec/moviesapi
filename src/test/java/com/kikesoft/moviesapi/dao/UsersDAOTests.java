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

import com.kikesoft.moviesapi.entity.UserEntity;
import com.kikesoft.moviesapi.exception.DuplicatedItemException;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.repository.UserRepository;
import com.kikesoft.moviesapi.vo.UserVO;

@ExtendWith(MockitoExtension.class)
class UsersDAOTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UsersDAO usersDAO;

    @Test
    void findAll_returnsAllMappedUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                buildEntity(1L, "alice", "secret1"),
                buildEntity(2L, "bob", "secret2")));

        List<UserVO> result = usersDAO.findAll();

        assertEquals(2, result.size());
        assertEquals("alice", result.get(0).getUsername());
        assertEquals("bob", result.get(1).getUsername());
        assertNull(result.get(0).getPassword());
        assertNull(result.get(1).getPassword());
        verify(userRepository).findAll();
    }

    @Test
    void findById_whenUserExists_returnsMappedUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildEntity(1L, "alice", "secret1")));

        UserVO result = usersDAO.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("alice", result.getUsername());
        assertNull(result.getPassword());
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_whenUserDoesNotExist_throwsItemNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> usersDAO.findById(99L));

        assertEquals("User with id 99 not found", exception.getMessage());
        verify(userRepository).findById(99L);
    }

    @Test
    void add_whenUsernameDoesNotExist_persistsAndReturnsUser() {
        UserEntity savedEntity = buildEntity(3L, "charlie", "pass123");
        UserVO userToAdd = new UserVO(null, "charlie", "pass123");

        when(userRepository.findByUsername("charlie")).thenReturn(Optional.empty());
        when(userRepository.save(argThat(entity -> entity != null
                && entity.isNew()
                && entity.getId() == null
                && "charlie".equals(entity.getUsername())
                && "pass123".equals(entity.getPassword())))).thenReturn(savedEntity);

        UserVO result = usersDAO.add(userToAdd);

        assertEquals(3L, result.getId());
        assertEquals("charlie", result.getUsername());
        assertNull(result.getPassword());
        verify(userRepository).findByUsername("charlie");
        verify(userRepository).save(argThat(entity -> entity != null && entity.isNew()));
    }

    @Test
    void add_whenUsernameAlreadyExists_throwsDuplicatedItemException() {
        UserVO userToAdd = new UserVO(null, "alice", "pass123");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(buildEntity(1L, "alice", "secret1")));

        DuplicatedItemException exception = assertThrows(DuplicatedItemException.class,
                () -> usersDAO.add(userToAdd));

        assertEquals("User with username 'alice' already exists", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void add_whenUserIsNull_returnsNull() {
        UserVO result = usersDAO.add(null);

        assertNull(result);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    private UserEntity buildEntity(Long id, String username, String password) {
        return new UserEntity(id, username, password);
    }
}
