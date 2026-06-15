package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.BillCreateDTO;
import com.akademikplus.akademik_plus.dto.BillResponseDTO;
import com.akademikplus.akademik_plus.exception.ErrorResponse;
import com.akademikplus.akademik_plus.service.BillService;
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

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Bills", description = "Bill management — admin issues, students pay individually")
public class BillController {

    private final BillService billService;

    @Operation(summary = "Get all bills (admin only)")
    @GetMapping
    public List<BillResponseDTO> getAll() {
        return billService.getAllBills();
    }

    @Operation(summary = "Get bills for a specific user (admin only)")
    @GetMapping("/user/{userId}")
    public List<BillResponseDTO> getByUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        return billService.getBillsByUserId(userId);
    }

    @Operation(summary = "Get bills for the currently authenticated user")
    @GetMapping("/my")
    public List<BillResponseDTO> getMy(Principal principal) {
        return billService.getBillsForCurrentUser(principal.getName());
    }

    @Operation(summary = "Get bill by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bill found"),
            @ApiResponse(responseCode = "404", description = "Bill not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public BillResponseDTO getById(
            @Parameter(description = "Bill ID") @PathVariable Long id) {
        return billService.getById(id);
    }

    @Operation(summary = "Create a new bill for a user (admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bill created"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<BillResponseDTO> create(
            @RequestBody BillCreateDTO dto, Principal principal) {
        return new ResponseEntity<>(billService.createBill(dto, principal.getName()), HttpStatus.CREATED);
    }

    @Operation(summary = "Pay a bill via Stripe (student)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bill paid successfully"),
            @ApiResponse(responseCode = "400", description = "Bill already paid, cancelled, or Stripe error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bill not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/pay")
    public ResponseEntity<BillResponseDTO> pay(
            @Parameter(description = "Bill ID") @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(billService.payBill(id, principal.getName()));
    }

    @Operation(summary = "Cancel a bill (admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bill cancelled"),
            @ApiResponse(responseCode = "400", description = "Cannot cancel a paid bill",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bill not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<BillResponseDTO> cancel(
            @Parameter(description = "Bill ID") @PathVariable Long id) {
        return ResponseEntity.ok(billService.cancelBill(id));
    }
}
