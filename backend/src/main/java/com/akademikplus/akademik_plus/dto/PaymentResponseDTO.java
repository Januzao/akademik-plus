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

    @Schema(description = "Payment ID", example = "12")
    private Long id;

    @Schema(description = "Amount charged in PLN", example = "650.00")
    private BigDecimal amount;

    @Schema(description = "Description of the payment", example = "November rent")
    private String paidFor;

    @Schema(description = "Date the payment was made", example = "2024-11-01")
    private LocalDate paymentDate;

    @Schema(description = "Payment status", example = "COMPLETED")
    private PaymentStatus status;

    @Schema(description = "Stripe Charge ID", example = "ch_3OA...")
    private String transactionId;

    @Schema(description = "Stripe Refund ID — present only if refunded", example = "re_3OA...")
    private String refundId;

    @Schema(description = "Timestamp of the refund")
    private LocalDateTime refundedAt;

    @Schema(description = "ID of the tenant who made the payment", example = "5")
    private Long tenantId;

    @Schema(description = "Full name of the tenant", example = "Jan Kowalski")
    private String tenantName;

    @Schema(description = "Room number of the tenant", example = "101A")
    private String roomNumber;
}
