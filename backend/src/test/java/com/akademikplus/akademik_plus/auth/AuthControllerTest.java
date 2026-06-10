package com.akademikplus.akademik_plus.auth;

import com.akademikplus.akademik_plus.exception.ValidationException;
import com.akademikplus.akademik_plus.security.JwtService;
import com.akademikplus.akademik_plus.security.UserDetailServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AuthControllerTest {

    @Autowired private WebApplicationContext context;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private MockMvc mockMvc;

    @MockitoBean AuthService authService;
    @MockitoBean JwtService jwtService;
    @MockitoBean UserDetailServiceImpl userDetailService;
    @MockitoBean TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private AuthResponseDTO buildAuthResponse() {
        return new AuthResponseDTO("access-token-123", "refresh-token-456");
    }

    @Test
    void register_returnsOk_whenValid() throws Exception {
        AuthRequestDTO req = new AuthRequestDTO();
        req.setEmail("new@example.com");
        req.setPassword("password123");

        when(authService.register(any())).thenReturn(buildAuthResponse());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-456"));
    }

    @Test
    void register_returnsBadRequest_whenEmailInvalid() throws Exception {
        AuthRequestDTO req = new AuthRequestDTO();
        req.setEmail("not-an-email");
        req.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_returnsBadRequest_whenEmailAlreadyExists() throws Exception {
        AuthRequestDTO req = new AuthRequestDTO();
        req.setEmail("existing@example.com");
        req.setPassword("password123");

        when(authService.register(any()))
                .thenThrow(new ValidationException("Email is already registered: existing@example.com"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_returnsOk_whenValid() throws Exception {
        AuthRequestDTO req = new AuthRequestDTO();
        req.setEmail("jan@example.com");
        req.setPassword("password123");

        when(authService.login(any())).thenReturn(buildAuthResponse());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void refresh_returnsOk_whenValid() throws Exception {
        RefreshTokenRequestDTO req = new RefreshTokenRequestDTO();
        req.setRefreshToken("valid-refresh-token");

        when(authService.refresh("valid-refresh-token")).thenReturn(buildAuthResponse());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token-123"));
    }

    @Test
    @WithMockUser(username = "jan@example.com")
    void logout_returnsNoContent() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "jan@example.com")
    void changePassword_returnsNoContent_whenValid() throws Exception {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("oldPassword");
        dto.setNewPassword("newPassword123");

        doNothing().when(authService).changePassword(anyString(), any());

        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void forgotPassword_returnsNoContent() throws Exception {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setEmail("jan@example.com");

        doNothing().when(authService).forgotPassword("jan@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void resetPassword_returnsNoContent_whenValid() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken("reset-token-123");
        dto.setNewPassword("newPassword123");

        doNothing().when(authService).resetPassword(any());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }
}
