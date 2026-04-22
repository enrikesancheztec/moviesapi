package com.kikesoft.moviesapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.kikesoft.moviesapi.entity.ProducerEntity;
import com.kikesoft.moviesapi.repository.MovieRepository;
import com.kikesoft.moviesapi.repository.ProducerRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProducersControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private MovieRepository movieRepository;

    private Long producerId;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
        producerRepository.deleteAll();
        producerId = createProducer("John Smith", "Award-winning producer.").getId();
    }

    @Test
    void getProducerById_returnsExpectedProducer() throws Exception {
        mockMvc.perform(get("/producers/{id}", producerId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(producerId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("John Smith"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.profile").value("Award-winning producer."));
    }

    @Test
    void getProducerById_notExistingProducer() throws Exception {
        mockMvc.perform(get("/producers/999999"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void addProducer_returnsCreatedAndPersistsProducer() throws Exception {
        mockMvc.perform(post("/producers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Jane Doe",
                          "profile": "Independent producer."
                        }
                        """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Jane Doe"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.profile").value("Independent producer."));

        org.junit.jupiter.api.Assertions.assertTrue(producerRepository.findByName("Jane Doe").isPresent());
    }

    @Test
    void addProducer_whenNameAlreadyExists_returnsConflict() throws Exception {
        mockMvc.perform(post("/producers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "John Smith",
                          "profile": "Another profile."
                        }
                        """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isConflict())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value("Producer with name John Smith already exists"));
    }

    @Test
    void updateProducer_returnsUpdatedProducerAndPersistsChange() throws Exception {
        mockMvc.perform(put("/producers/{id}", producerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "John Smith",
                          "profile": "Updated producer profile."
                        }
                        """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.id").value(producerId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("John Smith"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.profile").value("Updated producer profile."));

        mockMvc.perform(get("/producers/{id}", producerId))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.profile").value("Updated producer profile."));
    }

    @Test
    void updateProducer_withMismatchedId_returnsBadRequest() throws Exception {
        Long mismatchedId = producerId + 98;

        mockMvc.perform(put("/producers/{id}", producerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                          "id": %d,
                          "name": "John Smith",
                          "profile": "Award-winning producer."
                        }
                        """, mismatchedId)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value("Path id " + producerId + " does not match request body id " + mismatchedId));
    }

    @Test
    void updateProducer_whenDuplicateNameExists_returnsConflict() throws Exception {
        ProducerEntity secondProducer = createProducer("Jane Doe", "Independent producer.");

        mockMvc.perform(put("/producers/{id}", secondProducer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "John Smith",
                          "profile": "Independent producer."
                        }
                        """))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isConflict())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value("Producer with name John Smith already exists with id " + producerId));
    }

    private ProducerEntity createProducer(String name, String profile) {
        ProducerEntity producer = new ProducerEntity(null, name, profile);
        producer.setNew(true);
        return producerRepository.save(producer);
    }
}
