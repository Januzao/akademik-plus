package com.akademikplus.akademik_plus.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {
    private Long userId;
    private String paidFor;
    private String stripeToken;
    private BigDecimal amount;
}
