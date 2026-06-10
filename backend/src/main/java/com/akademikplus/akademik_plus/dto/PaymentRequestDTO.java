package com.akademikplus.akademik_plus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Payment request payload")
public class PaymentRequestDTO {

    @Schema(description = "ID of the user making the payment", example = "5")
    private Long userId;

    @Schema(description = "Description of what is being paid for", example = "November rent")
    private String paidFor;

    @Schema(description = "Stripe card token obtained from the frontend", example = "tok_visa")
    private String stripeToken;

    @Schema(description = "Amount to charge in PLN", example = "650.00")
    private BigDecimal amount;
}
