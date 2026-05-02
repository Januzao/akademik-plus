package com.akademikplus.akademik_plus.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentResponseDTO {
    private Long id;
    private BigDecimal amount;
    private String paidFor;
    private LocalDate paymentDate;
    private String status;
}
