package com.kikesoft.moviesapi.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kikesoft.moviesapi.service.ProducersService;
import com.kikesoft.moviesapi.vo.ProducerVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller that exposes producer operations.
 *
 * @author Enrique Sanchez
 */
@RestController
@RequestMapping("/producers")
@Tag(name = "Producers", description = "Operations to retrieve, create and update producers")
class ProducersController {
    private static final Logger LOGGER = LogManager.getLogger(ProducersController.class);

    @Autowired
    ProducersService producersService;

    /**
     * Retrieves a producer by id.
     *
     * @param id producer identifier
     * @return producer representation
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get producer by id", description = "Returns a producer when the identifier exists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producer found", content = @Content(schema = @Schema(implementation = ProducerVO.class))),
            @ApiResponse(responseCode = "404", description = "Producer not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-19T10:00:00\",\"message\":\"Producer with id 99 not found\"}")))
    })
    ResponseEntity<ProducerVO> getById(@PathVariable Long id) {
        LOGGER.debug("GET /producers/{} - fetching producer by id", id);
        ProducerVO producer = producersService.findById(id);
        LOGGER.debug("GET /producers/{} - producer found: {}", id, producer);
        return ResponseEntity.ok(producer);
    }

    /**
     * Retrieves all producers.
     *
     * @return list of producers
     */
    @GetMapping
    @Operation(summary = "Get all producers", description = "Returns all producers currently stored")
    @ApiResponse(responseCode = "200", description = "Producers retrieved", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProducerVO.class))))
    ResponseEntity<List<ProducerVO>> getAll() {
        LOGGER.debug("GET /producers - fetching all producers");
        List<ProducerVO> producers = producersService.findAll();
        LOGGER.debug("GET /producers - retrieved {} producers: {}", producers.size(), producers);
        return ResponseEntity.ok(producers);
    }

    /**
     * Creates a new producer.
     *
     * @param producerVO producer payload to create
     * @return persisted producer representation
     */
    @PostMapping
    @Operation(summary = "Create producer", description = "Creates a producer when the payload is valid and not duplicated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producer created", content = @Content(schema = @Schema(implementation = ProducerVO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"name\":\"Name is mandatory\"}"))),
            @ApiResponse(responseCode = "409", description = "Producer already exists", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-19T10:00:00\",\"message\":\"Producer with name John Smith already exists\"}")))
    })
    ResponseEntity<ProducerVO> addNew(@Valid @RequestBody ProducerVO producerVO) {
        LOGGER.debug("POST /producers - creating producer with name '{}'", producerVO.getName());
        ProducerVO savedProducer = producersService.add(producerVO);
        if (savedProducer == null) {
            LOGGER.warn("POST /producers - request returned null saved producer");
            return ResponseEntity.badRequest().build();
        }
        LOGGER.debug("POST /producers - producer created with id {}: {}", savedProducer.getId(), savedProducer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProducer);
    }

    /**
     * Updates an existing producer.
     *
     * @param id producer identifier
     * @param producerVO producer payload with updated values
     * @return updated producer representation
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update producer", description = "Updates an existing producer by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producer updated", content = @Content(schema = @Schema(implementation = ProducerVO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payload or id mismatch", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-19T10:00:00\",\"message\":\"Path id 3 does not match request body id 99\"}"))),
            @ApiResponse(responseCode = "404", description = "Producer not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-19T10:00:00\",\"message\":\"Producer with id 3 not found\"}"))),
            @ApiResponse(responseCode = "409", description = "Producer duplicated by name", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-19T10:00:00\",\"message\":\"Producer with name John Smith already exists with id 7\"}")))
    })
    ResponseEntity<ProducerVO> update(@PathVariable Long id, @Valid @RequestBody ProducerVO producerVO) {
        LOGGER.debug("PUT /producers/{} - updating producer with payload id {}", id, producerVO.getId());
        ProducerVO updatedProducer = producersService.update(id, producerVO);
        if (updatedProducer == null) {
            LOGGER.warn("PUT /producers/{} - request returned null updated producer", id);
            return ResponseEntity.badRequest().build();
        }
        LOGGER.debug("PUT /producers/{} - producer updated successfully: {}", id, updatedProducer);
        return ResponseEntity.ok(updatedProducer);
    }
}