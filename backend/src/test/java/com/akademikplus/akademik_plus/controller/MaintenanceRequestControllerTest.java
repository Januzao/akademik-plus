package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.MaintenanceRequestReqDTO;
import com.akademikplus.akademik_plus.dto.MaintenanceRequestRespDTO;
import com.akademikplus.akademik_plus.enums.MaintenanceCategory;
import com.akademikplus.akademik_plus.enums.MaintenancePriority;
import com.akademikplus.akademik_plus.enums.MaintenanceStatus;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.security.JwtService;
import com.akademikplus.akademik_plus.security.UserDetailServiceImpl;
import com.akademikplus.akademik_plus.service.MaintenanceRequestService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class MaintenanceRequestControllerTest {

    @Autowired private WebApplicationContext context;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private MockMvc mockMvc;

    @MockitoBean MaintenanceRequestService maintenanceRequestService;
    @MockitoBean JwtService jwtService;
    @MockitoBean UserDetailServiceImpl userDetailService;
    @MockitoBean TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private MaintenanceRequestRespDTO buildResponse(Long id) {
        MaintenanceRequestRespDTO dto = new MaintenanceRequestRespDTO();
        dto.setId(id);
        dto.setCategory(MaintenanceCategory.PLUMBING);
        dto.setPriority(MaintenancePriority.HIGH);
        dto.setStatus(MaintenanceStatus.PENDING);
        dto.setDescription("Leaking pipe");
        dto.setRequestDate(LocalDate.now());
        dto.setRoomNumber("101A");
        dto.setTenantName("Jan Kowalski");
        return dto;
    }

    private MaintenanceRequestReqDTO buildRequest() {
        MaintenanceRequestReqDTO req = new MaintenanceRequestReqDTO();
        req.setRoomId(1L);
        req.setCategory(MaintenanceCategory.PLUMBING);
        req.setPriority(MaintenancePriority.HIGH);
        req.setDescription("Leaking pipe");
        return req;
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAll_returnsOkWithList() throws Exception {
        when(maintenanceRequestService.findAll()).thenReturn(List.of(buildResponse(1L)));

        mockMvc.perform(get("/api/maintenance-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void getAll_returnsOk_forStudentRole() throws Exception {
        when(maintenanceRequestService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/maintenance-requests"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getById_returnsRequest_whenFound() throws Exception {
        when(maintenanceRequestService.findById(1L)).thenReturn(buildResponse(1L));

        mockMvc.perform(get("/api/maintenance-requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("PLUMBING"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getById_returnsNotFound_whenMissing() throws Exception {
        when(maintenanceRequestService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Maintenance request not found with id: 99"));

        mockMvc.perform(get("/api/maintenance-requests/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void create_returnsCreated_whenValid() throws Exception {
        when(maintenanceRequestService.createRequest(any(), anyLong())).thenReturn(buildResponse(1L));

        mockMvc.perform(post("/api/maintenance-requests")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateStatus_returnsOk() throws Exception {
        MaintenanceRequestRespDTO updated = buildResponse(1L);
        updated.setStatus(MaintenanceStatus.IN_PROGRESS);
        when(maintenanceRequestService.updateStatus(eq(1L), eq(MaintenanceStatus.IN_PROGRESS)))
                .thenReturn(updated);

        mockMvc.perform(patch("/api/maintenance-requests/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void updateStatus_returnsForbidden_forStudentRole() throws Exception {
        mockMvc.perform(patch("/api/maintenance-requests/1/status")
                        .param("status", "RESOLVED"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/maintenance-requests/1"))
                .andExpect(status().isNoContent());
    }
}
