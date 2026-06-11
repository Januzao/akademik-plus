package com.akademikplus.akademik_plus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Admin patch payload — room assignment and account status only")
public class AdminUserPatchDTO {

    @Schema(description = "ID of the room to assign, null to remove assignment", example = "3")
    private Long roomId;

    @Schema(description = "Whether the account is active", example = "true")
    @NotNull(message = "isActive is required")
    private Boolean isActive;
}
