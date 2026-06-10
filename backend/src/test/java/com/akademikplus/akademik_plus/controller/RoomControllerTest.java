package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.RoomRequestDTO;
import com.akademikplus.akademik_plus.dto.RoomResponseDTO;
import com.akademikplus.akademik_plus.enums.OccupancyStatus;
import com.akademikplus.akademik_plus.enums.RoomType;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.exception.ValidationException;
import com.akademikplus.akademik_plus.security.JwtService;
import com.akademikplus.akademik_plus.security.UserDetailServiceImpl;
import com.akademikplus.akademik_plus.service.RoomService;
import com.akademikplus.akademik_plus.service.TokenBlacklistService;
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
class RoomControllerTest {

    @Autowired private WebApplicationContext context;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private MockMvc mockMvc;

    @MockitoBean RoomService roomService;
    @MockitoBean JwtService jwtService;
    @MockitoBean UserDetailServiceImpl userDetailService;
    @MockitoBean TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private RoomResponseDTO buildResponse(Long id) {
        RoomResponseDTO dto = new RoomResponseDTO();
        dto.setId(id);
        dto.setRoomNumber("101A");
        dto.setRoomType(RoomType.DOUBLE);
        dto.setOccupancyStatus(OccupancyStatus.VACANT);
        dto.setOccupiedPlaces(0);
        dto.setTotalPlaces(2);
        dto.setFloorNumber(1);
        dto.setRentPrice(new BigDecimal("650.00"));
        return dto;
    }

    private RoomRequestDTO buildRequest() {
        RoomRequestDTO req = new RoomRequestDTO();
        req.setRoomNumber("101A");
        req.setRoomType(RoomType.DOUBLE);
        req.setTotalPlaces(2);
        req.setFloorNumber(1);
        req.setRentPrice(new BigDecimal("650.00"));
        return req;
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAll_returnsOkWithList() throws Exception {
        when(roomService.findAll()).thenReturn(List.of(buildResponse(1L)));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].roomNumber").value("101A"));
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void getAll_returnsOk_forStudentRole() throws Exception {
        when(roomService.findAll()).thenReturn(List.of(buildResponse(1L)));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getById_returnsRoom_whenFound() throws Exception {
        when(roomService.findById(1L)).thenReturn(buildResponse(1L));

        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value("101A"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getById_returnsNotFound_whenMissing() throws Exception {
        when(roomService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Room not found with id: 99"));

        mockMvc.perform(get("/api/rooms/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void create_returnsCreated_whenValid() throws Exception {
        when(roomService.create(any())).thenReturn(buildResponse(1L));

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void create_returnsBadRequest_whenValidationFails() throws Exception {
        RoomRequestDTO invalid = new RoomRequestDTO();

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void update_returnsBadRequest_whenCapacityViolated() throws Exception {
        when(roomService.update(eq(1L), any()))
                .thenThrow(new ValidationException("Cannot decrease total places"));

        mockMvc.perform(put("/api/rooms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void create_returnsForbidden_forStudentRole() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isForbidden());
    }
}
