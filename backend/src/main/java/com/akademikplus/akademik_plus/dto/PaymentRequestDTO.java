package com.akademikplus.akademik_plus.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {
    private String PaidFor;
    private String transactionId;
    private BigDecimal amount;
}
