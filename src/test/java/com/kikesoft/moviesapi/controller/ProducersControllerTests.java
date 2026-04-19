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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.kikesoft.moviesapi.exception.DuplicatedItemException;
import com.kikesoft.moviesapi.exception.ItemIdMismatchException;
import com.kikesoft.moviesapi.exception.ItemNotFoundException;
import com.kikesoft.moviesapi.service.ProducersService;
import com.kikesoft.moviesapi.vo.ProducerVO;

@WebMvcTest(ProducersController.class)
class ProducersControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProducersService producersService;

    @Test
    void getProducerById_returnsExpectedProducer() throws Exception {
        when(producersService.findById(1L)).thenReturn(new ProducerVO(1L, "John Smith", "Award-winning producer."));

        mockMvc.perform(get("/producers/1"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("John Smith"));
    }

    @Test
    void getProducerById_notExistingProducer() throws Exception {
        when(producersService.findById(2L)).thenThrow(new ItemNotFoundException("Producer with id 2 not found"));

        mockMvc.perform(get("/producers/2"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getAll_returnsProducerList() throws Exception {
        when(producersService.findAll()).thenReturn(List.of(
                new ProducerVO(1L, "John Smith", "Award-winning producer."),
                new ProducerVO(2L, "Jane Doe", "Independent producer.")));

        mockMvc.perform(get("/producers"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[1].id").value(2));
    }

    @Test
    void addNewProducer_withoutId_returnsCreated() throws Exception {
        when(producersService.add(argThat(producer -> producer != null && producer.getId() == null)))
                .thenReturn(new ProducerVO(3L, "John Smith", "Award-winning producer."));

        mockMvc.perform(post("/producers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "John Smith",
                                  "profile": "Award-winning producer."
                                }
                                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(3))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("John Smith"));
    }

    @Test
    void addNewProducer_withId_returnsBadRequest() throws Exception {
        when(producersService.add(argThat(producer -> producer != null && producer.getId() != null))).thenReturn(null);

        mockMvc.perform(post("/producers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 99,
                                  "name": "John Smith",
                                  "profile": "Award-winning producer."
                                }
                                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateProducer_returnsOk() throws Exception {
        when(producersService.update(argThat(id -> id != null && id.equals(3L)),
                argThat(producer -> producer != null && producer.getId() == null && "John Smith".equals(producer.getName()))))
                .thenReturn(new ProducerVO(3L, "John Smith", "Award-winning producer."));

        mockMvc.perform(put("/producers/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "John Smith",
                                  "profile": "Award-winning producer."
                                }
                                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(3))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("John Smith"));
    }

    @Test
    void updateProducer_withMismatchedId_returnsBadRequest() throws Exception {
        when(producersService.update(argThat(id -> id != null && id.equals(3L)),
                argThat(producer -> producer != null && Long.valueOf(99L).equals(producer.getId()))))
                .thenThrow(new ItemIdMismatchException("Path id 3 does not match request body id 99"));

        mockMvc.perform(put("/producers/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 99,
                                  "name": "John Smith",
                                  "profile": "Award-winning producer."
                                }
                                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value("Path id 3 does not match request body id 99"));
    }

    @Test
    void addNewProducer_whenProducerIsDuplicated_returnsConflict() throws Exception {
        when(producersService.add(argThat(producer -> producer != null && producer.getId() == null)))
                .thenThrow(new DuplicatedItemException("Producer with name John Smith already exists"));

        mockMvc.perform(post("/producers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "John Smith",
                                  "profile": "Award-winning producer."
                                }
                                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isConflict())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value("Producer with name John Smith already exists"));
    }

    @Test
    void updateProducer_whenProducerNotFound_returnsNotFound() throws Exception {
        when(producersService.update(argThat(id -> id != null && id.equals(999L)),
                argThat(producer -> producer != null && "John Smith".equals(producer.getName()))))
                .thenThrow(new ItemNotFoundException("Producer with id 999 not found"));

        mockMvc.perform(put("/producers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "John Smith",
                                  "profile": "Award-winning producer."
                                }
                                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value("Producer with id 999 not found"));
    }

    @Test
    void updateProducer_whenServiceReturnsNull_returnsBadRequest() throws Exception {
        when(producersService.update(argThat(id -> id != null && id.equals(3L)),
                argThat(producer -> producer != null && "John Smith".equals(producer.getName()))))
                .thenReturn(null);

        mockMvc.perform(put("/producers/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "John Smith",
                                  "profile": "Award-winning producer."
                                }
                                """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest());
    }
}