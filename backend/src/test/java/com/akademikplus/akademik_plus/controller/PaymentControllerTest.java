package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.PaymentRequestDTO;
import com.akademikplus.akademik_plus.dto.PaymentResponseDTO;
import com.akademikplus.akademik_plus.enums.PaymentStatus;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.exception.ValidationException;
import com.akademikplus.akademik_plus.security.JwtService;
import com.akademikplus.akademik_plus.security.UserDetailServiceImpl;
import com.akademikplus.akademik_plus.service.PaymentService;
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
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class PaymentControllerTest {

    @Autowired private WebApplicationContext context;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private MockMvc mockMvc;

    @MockitoBean PaymentService paymentService;
    @MockitoBean JwtService jwtService;
    @MockitoBean UserDetailServiceImpl userDetailService;
    @MockitoBean TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private PaymentResponseDTO buildResponse(Long id) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(id);
        dto.setAmount(new BigDecimal("650.00"));
        dto.setPaidFor("November rent");
        dto.setPaymentDate(LocalDate.now());
        dto.setStatus(PaymentStatus.COMPLETED);
        dto.setTransactionId("ch_test_123");
        dto.setTenantId(1L);
        dto.setTenantName("Jan Kowalski");
        return dto;
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAll_returnsOkWithList() throws Exception {
        when(paymentService.findAll()).thenReturn(List.of(buildResponse(1L)));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void getAll_returnsOk_forStudentRole() throws Exception {
        when(paymentService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getById_returnsPayment_whenFound() throws Exception {
        when(paymentService.findById(1L)).thenReturn(buildResponse(1L));

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("ch_test_123"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getById_returnsNotFound_whenMissing() throws Exception {
        when(paymentService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Payment not found with id: 99"));

        mockMvc.perform(get("/api/payments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void create_returnsCreated() throws Exception {
        PaymentRequestDTO req = new PaymentRequestDTO();
        req.setUserId(1L);
        req.setAmount(new BigDecimal("650.00"));
        req.setPaidFor("November rent");
        req.setStripeToken("tok_test");

        when(paymentService.createPayment(any())).thenReturn(buildResponse(1L));

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void create_returnsBadRequest_whenAmountInvalid() throws Exception {
        PaymentRequestDTO req = new PaymentRequestDTO();
        req.setUserId(1L);
        req.setAmount(BigDecimal.ZERO);
        req.setPaidFor("Test");

        when(paymentService.createPayment(any()))
                .thenThrow(new ValidationException("Amount must be positive"));

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void refund_returnsOk() throws Exception {
        PaymentResponseDTO refundedResponse = buildResponse(1L);
        refundedResponse.setStatus(PaymentStatus.REFUNDED);
        when(paymentService.refund(1L)).thenReturn(refundedResponse);

        mockMvc.perform(post("/api/payments/1/refund"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void refund_returnsForbidden_forStudentRole() throws Exception {
        mockMvc.perform(post("/api/payments/1/refund"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/payments/1"))
                .andExpect(status().isNoContent());
    }
}
