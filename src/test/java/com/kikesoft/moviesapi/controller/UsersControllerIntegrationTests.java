package com.kikesoft.moviesapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kikesoft.moviesapi.entity.Role;
import com.kikesoft.moviesapi.entity.UserEntity;
import com.kikesoft.moviesapi.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsersControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long userId;
    private String userBearerToken;
    private String adminBearerToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userId = createUser("alice", passwordEncoder.encode("secret1"), Role.USER).getId();
        createUser("admin", passwordEncoder.encode("secret1"), Role.ADMIN);
        userBearerToken = loginAndGetToken("alice", "secret1");
        adminBearerToken = loginAndGetToken("admin", "secret1");
    }

    @Test
    void getAll_withoutToken_returnsUnauthorized() throws Exception {
      mockMvc.perform(get("/users"))
          .andExpect(status().isUnauthorized());
    }

    @Test
        void getAll_withUserRole_returnsForbidden() throws Exception {
      mockMvc.perform(get("/users")
          .header("Authorization", userBearerToken))
            .andExpect(status().isForbidden());
        }

        @Test
        void getAll_withAdminRole_returnsUserList() throws Exception {
      mockMvc.perform(get("/users")
          .header("Authorization", adminBearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userId))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
        void getById_withUserRole_returnsForbidden() throws Exception {
      mockMvc.perform(get("/users/{id}", userId)
          .header("Authorization", userBearerToken))
            .andExpect(status().isForbidden());
        }

        @Test
        void getById_whenAdminRoleAndUserExists_returnsUser() throws Exception {
      mockMvc.perform(get("/users/{id}", userId)
          .header("Authorization", adminBearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
        void getById_whenAdminRoleAndUserDoesNotExist_returnsNotFound() throws Exception {
      mockMvc.perform(get("/users/999999")
          .header("Authorization", adminBearerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void addUser_withValidPayload_returnsCreatedAndPersists() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "bob",
                                  "password": "secret2"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("bob"))
                .andExpect(jsonPath("$.password").doesNotExist());

        org.junit.jupiter.api.Assertions.assertTrue(userRepository.findByUsername("bob").isPresent());
    }

    @Test
    void addUser_whenUsernameAlreadyExists_returnsConflict() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "password": "anotherpass"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User with username 'alice' already exists"));
    }

    @Test
    void addUser_withBlankUsername_returnsBadRequest() throws Exception {
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
    void addUser_withBlankPassword_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "charlie",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUser_passwordIsNeverStoredInResponse() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "dave",
                                  "password": "topsecret"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    private UserEntity createUser(String username, String password, Role role) {
        UserEntity user = new UserEntity(null, username, password);
      user.setRole(role);
        user.setNew(true);
        return userRepository.save(user);
    }

    private String loginAndGetToken(String username, String password) {
      try {
        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {
                      \"username\": \"%s\",
                      \"password\": \"%s\"
                    }
                    """, username, password)))
            .andExpect(status().isOk())
            .andReturn();
        return "Bearer " + result.getResponse().getContentAsString();
      } catch (Exception e) {
        throw new IllegalStateException("Unable to authenticate test user", e);
      }
    }
}
