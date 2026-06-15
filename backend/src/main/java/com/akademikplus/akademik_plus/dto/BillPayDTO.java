package com.akademikplus.akademik_plus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request payload for paying a bill via Stripe")
public class BillPayDTO {

    @Schema(description = "Stripe card token", example = "tok_visa")
    private String stripeToken;
}
