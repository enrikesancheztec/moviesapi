package com.kikesoft.moviesapi.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kikesoft.moviesapi.dao.UsersDAO;
import com.kikesoft.moviesapi.vo.UserVO;

@ExtendWith(MockitoExtension.class)
class UsersServiceTests {

    @Mock
    private UsersDAO usersDAO;

    @InjectMocks
    private UsersService usersService;

    @Test
    void findAll_delegatesToDao() {
        List<UserVO> users = List.of(new UserVO(1L, "alice", null));

        when(usersDAO.findAll()).thenReturn(users);

        List<UserVO> result = usersService.findAll();

        assertEquals(users, result);
        verify(usersDAO).findAll();
    }

    @Test
    void findById_delegatesToDao() {
        UserVO user = new UserVO(1L, "alice", null);

        when(usersDAO.findById(1L)).thenReturn(user);

        UserVO result = usersService.findById(1L);

        assertEquals(user, result);
        verify(usersDAO).findById(1L);
    }

    @Test
    void add_delegatesToDao() {
        UserVO input = new UserVO(null, "alice", "secret1");
        UserVO saved = new UserVO(1L, "alice", null);

        when(usersDAO.add(input)).thenReturn(saved);

        UserVO result = usersService.add(input);

        assertEquals(saved, result);
        assertNull(result.getPassword());
        verify(usersDAO).add(input);
    }

    @Test
    void add_whenDaoReturnsNull_returnsNull() {
        when(usersDAO.add(null)).thenReturn(null);

        UserVO result = usersService.add(null);

        assertNull(result);
        verify(usersDAO).add(null);
    }
}
