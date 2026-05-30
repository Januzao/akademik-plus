package com.akademikplus.akademik_plus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Payment request payload")
public class PaymentRequestDTO {
    private Long userId;
    private String paidFor;
    private String stripeToken;
    private BigDecimal amount;
}
