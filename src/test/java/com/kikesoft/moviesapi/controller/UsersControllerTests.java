package com.kikesoft.moviesapi.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kikesoft.moviesapi.exception.DuplicatedItemException;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.service.UsersService;
import com.kikesoft.moviesapi.vo.UserVO;

@WebMvcTest(UsersController.class)
class UsersControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsersService usersService;

    @Test
    void getAll_returnsUserList() throws Exception {
        when(usersService.findAll()).thenReturn(List.of(
                new UserVO(1L, "alice", null),
                new UserVO(2L, "bob", null)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("bob"));
    }

    @Test
    void getById_whenUserExists_returnsUser() throws Exception {
        when(usersService.findById(1L)).thenReturn(new UserVO(1L, "alice", null));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void getById_whenUserDoesNotExist_returnsNotFound() throws Exception {
        when(usersService.findById(99L)).thenThrow(new ItemNotFoundException("User with id 99 not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addNewUser_withValidPayload_returnsCreated() throws Exception {
        when(usersService.add(argThat(u -> u != null && "alice".equals(u.getUsername()))))
                .thenReturn(new UserVO(1L, "alice", null));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "password": "secret1"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void addNewUser_withBlankUsername_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "",
                                  "password": "secret1"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewUser_withBlankPassword_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addNewUser_whenUsernameAlreadyExists_returnsConflict() throws Exception {
        when(usersService.add(argThat(u -> u != null && "alice".equals(u.getUsername()))))
                .thenThrow(new DuplicatedItemException("User with username 'alice' already exists"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "password": "secret1"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User with username 'alice' already exists"));
    }
}
