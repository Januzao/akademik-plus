package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "Payment response payload")
public class PaymentResponseDTO {
    private Long id;
    private BigDecimal amount;
    private String paidFor;
    private LocalDate paymentDate;
    private PaymentStatus status;
    private String transactionId;
    private String refundId;
    private LocalDateTime refundedAt;

    private Long tenantId;
    private String tenantName;
    private String roomNumber;
}
