package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.UserRequestDTO;
import com.akademikplus.akademik_plus.dto.UserResponseDTO;
import com.akademikplus.akademik_plus.enums.Role;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.security.JwtService;
import com.akademikplus.akademik_plus.security.UserDetailServiceImpl;
import com.akademikplus.akademik_plus.service.TokenBlacklistService;
import com.akademikplus.akademik_plus.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserControllerTest {

    @Autowired private WebApplicationContext context;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private MockMvc mockMvc;

    @MockitoBean UserService userService;
    @MockitoBean JwtService jwtService;
    @MockitoBean UserDetailServiceImpl userDetailService;
    @MockitoBean TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private UserResponseDTO buildResponse(Long id, String email) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(id);
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setEmail(email);
        dto.setRole(Role.STUDENT);
        dto.setIsActive(true);
        dto.setBalance(BigDecimal.ZERO);
        return dto;
    }

    private UserRequestDTO buildRequest() {
        UserRequestDTO req = new UserRequestDTO();
        req.setFirstName("Jan");
        req.setLastName("Kowalski");
        req.setEmail("jan@example.com");
        req.setPassword("secret123");
        req.setRole(Role.STUDENT);
        return req;
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAll_returnsOkWithList() throws Exception {
        when(userService.findAll()).thenReturn(List.of(buildResponse(1L, "jan@example.com")));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("jan@example.com"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getById_returnsUser_whenFound() throws Exception {
        when(userService.findById(1L)).thenReturn(buildResponse(1L, "jan@example.com"));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jan@example.com"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getById_returnsNotFound_whenMissing() throws Exception {
        when(userService.findById(99L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void create_returnsCreated_whenValid() throws Exception {
        when(userService.create(any())).thenReturn(buildResponse(1L, "jan@example.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void create_returnsBadRequest_whenValidationFails() throws Exception {
        UserRequestDTO invalid = new UserRequestDTO();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void update_returnsUpdatedUser() throws Exception {
        when(userService.update(eq(1L), any())).thenReturn(buildResponse(1L, "updated@example.com"));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAll_returnsForbidden_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void getAll_returnsForbidden_whenStudentRole() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }
}
