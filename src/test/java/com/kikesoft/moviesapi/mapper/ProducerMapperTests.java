package com.kikesoft.moviesapi.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import com.kikesoft.moviesapi.entity.ProducerEntity;
import com.kikesoft.moviesapi.vo.ProducerVO;

class ProducerMapperTests {

    @Test
    void toVO_whenEntityIsNull_returnsNull() {
        ProducerVO result = ProducerMapper.toVO(null);

        assertNull(result);
    }

    @Test
    void toVO_whenEntityIsNotNull_mapsAllFields() {
        ProducerEntity entity = new ProducerEntity(1L, "John Smith", "Award-winning producer.");

        ProducerVO result = ProducerMapper.toVO(entity);

        assertEquals(1L, result.getId());
        assertEquals("John Smith", result.getName());
        assertEquals("Award-winning producer.", result.getProfile());
    }

    @Test
    void toEntity_whenProducerVOIsNull_returnsNull() {
        ProducerEntity result = ProducerMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void toEntity_whenProducerVOIsNotNull_mapsAllFields() {
        ProducerVO producerVO = new ProducerVO(2L, "Jane Doe", "Independent producer.");

        ProducerEntity result = ProducerMapper.toEntity(producerVO);

        assertEquals(2L, result.getId());
        assertEquals("Jane Doe", result.getName());
        assertEquals("Independent producer.", result.getProfile());
    }
}