package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.PaymentRequestDTO;
import com.akademikplus.akademik_plus.dto.PaymentResponseDTO;
import com.akademikplus.akademik_plus.exception.ErrorResponse;
import com.akademikplus.akademik_plus.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payments", description = "Payment processing and history")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Get all payments")
    @ApiResponse(responseCode = "200", description = "List of payments returned")
    @GetMapping
    public List<PaymentResponseDTO> getAll() {
        return paymentService.findAll();
    }

    @Operation(summary = "Get payments for the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "List of current user's payments")
    @GetMapping("/my")
    public List<PaymentResponseDTO> getMy(java.security.Principal principal) {
        return paymentService.findByCurrentUser(principal.getName());
    }

    @Operation(summary = "Get payment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public PaymentResponseDTO getById(
            @Parameter(description = "Payment ID") @PathVariable Long id) {
        return paymentService.findById(id);
    }

    @Operation(summary = "Create a new payment via Stripe",
            description = "Charges the card using a Stripe token and records the transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment created"),
            @ApiResponse(responseCode = "400", description = "Invalid amount or Stripe error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> create(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        return new ResponseEntity<>(paymentService.createPayment(paymentRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Refund a completed payment", description = "Admin only — issues a full refund via Stripe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refund issued successfully"),
            @ApiResponse(responseCode = "400", description = "Payment is not in COMPLETED status or has no transaction ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponseDTO> refund(
            @Parameter(description = "Payment ID") @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refund(id));
    }

    @Operation(summary = "Delete payment record", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Payment deleted"),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Payment ID") @PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
