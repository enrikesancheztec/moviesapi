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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kikesoft.moviesapi.service.UsersService;
import com.kikesoft.moviesapi.vo.UserVO;

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
 * REST controller that exposes user operations.
 *
 * @author Enrique Sanchez
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Operations to retrieve and create users")
class UsersController {
    private static final Logger LOGGER = LogManager.getLogger(UsersController.class);

    @Autowired
    UsersService usersService;

    /**
     * Retrieves all users.
     *
     * @return list of users (password is never included)
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Returns all users currently stored. Password is never included in the response.")
    @ApiResponse(responseCode = "200", description = "Users retrieved", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserVO.class))))
    ResponseEntity<List<UserVO>> getAll() {
        LOGGER.debug("GET /users - fetching all users");
        List<UserVO> users = usersService.findAll();
        LOGGER.debug("GET /users - retrieved {} users", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a user by id.
     *
     * @param id user identifier
     * @return user representation (password is never included)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by id", description = "Returns a user when the identifier exists. Password is never included in the response.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserVO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"timestamp\":\"2026-05-01T10:00:00\",\"message\":\"User with id 99 not found\"}")))
    })
    ResponseEntity<UserVO> getById(@PathVariable Long id) {
        LOGGER.debug("GET /users/{} - fetching user by id", id);
        UserVO user = usersService.findById(id);
        LOGGER.debug("GET /users/{} - user found: {}", id, user);
        return ResponseEntity.ok(user);
    }

    /**
     * Creates a new user.
     *
     * @param userVO user payload to create
     * @return persisted user representation (password is never included)
     */
    @PostMapping
    @Operation(summary = "Create user", description = "Creates a user when the payload is valid and the username is not duplicated. Password is never included in the response.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created", content = @Content(schema = @Schema(implementation = UserVO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"username\":\"Username is mandatory\",\"password\":\"Password is mandatory\"}"))),
            @ApiResponse(responseCode = "409", description = "Username already exists", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"timestamp\":\"2026-05-01T10:00:00\",\"message\":\"User with username 'john' already exists\"}")))
    })
    ResponseEntity<UserVO> addNew(@Valid @RequestBody UserVO userVO) {
        LOGGER.debug("POST /users - creating user with username '{}'", userVO.getUsername());
        UserVO savedUser = usersService.add(userVO);
        if (savedUser == null) {
            LOGGER.warn("POST /users - request returned null saved user");
            return ResponseEntity.badRequest().build();
        }
        LOGGER.debug("POST /users - user created with id {}", savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}
