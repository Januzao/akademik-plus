package com.akademikplus.akademik_plus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ArrearsEntryDTO {
    private Long userId;
    private String name;
    private String email;
    private String roomNumber;
    private BigDecimal balance;
    private BigDecimal monthlyRent;
    private BigDecimal deficit;
}
