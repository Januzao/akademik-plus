package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.AdminStatsDTO;
import com.akademikplus.akademik_plus.service.AdminStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Stats", description = "Occupancy and financial statistics for administrators")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @Operation(summary = "Get admin dashboard statistics — occupancy, arrears, room breakdown")
    @GetMapping("/stats")
    public AdminStatsDTO getStats() {
        return adminStatsService.getStats();
    }
}
