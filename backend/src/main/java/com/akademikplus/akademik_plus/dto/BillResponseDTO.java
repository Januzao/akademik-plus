package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.BillStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "Bill response payload")
public class BillResponseDTO {

    @Schema(description = "Bill ID", example = "1")
    private Long id;

    @Schema(description = "ID of the user the bill is for", example = "5")
    private Long userId;

    @Schema(description = "Full name of the user", example = "Jan Kowalski")
    private String userName;

    @Schema(description = "Email of the user", example = "jan.kowalski@example.com")
    private String userEmail;

    @Schema(description = "Room number of the user", example = "305")
    private String roomNumber;

    @Schema(description = "Name of the admin who issued the bill")
    private String issuedByName;

    @Schema(description = "Bill title", example = "November rent")
    private String title;

    @Schema(description = "Bill description")
    private String description;

    @Schema(description = "Amount in PLN", example = "850.00")
    private BigDecimal amount;

    @Schema(description = "Due date for payment", example = "2024-11-15")
    private LocalDate dueDate;

    @Schema(description = "Date the bill was issued", example = "2024-11-01")
    private LocalDate issuedDate;

    @Schema(description = "Bill status", example = "PENDING")
    private BillStatus status;

    @Schema(description = "Stripe transaction ID — present only if paid")
    private String transactionId;

    @Schema(description = "Date the bill was paid")
    private LocalDate paidDate;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
}
