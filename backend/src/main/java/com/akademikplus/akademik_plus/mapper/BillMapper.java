package com.akademikplus.akademik_plus.mapper;

import com.akademikplus.akademik_plus.dto.BillResponseDTO;
import com.akademikplus.akademik_plus.entity.Bill;
import org.springframework.stereotype.Component;

@Component
public class BillMapper {

    public BillResponseDTO toResponse(Bill bill) {
        BillResponseDTO dto = new BillResponseDTO();
        dto.setId(bill.getId());
        dto.setTitle(bill.getTitle());
        dto.setDescription(bill.getDescription());
        dto.setAmount(bill.getAmount());
        dto.setDueDate(bill.getDueDate());
        dto.setIssuedDate(bill.getIssuedDate());
        dto.setStatus(bill.getStatus());
        dto.setCreatedAt(bill.getCreatedAt());

        if (bill.getUser() != null) {
            dto.setUserId(bill.getUser().getId());
            dto.setUserName(bill.getUser().getFirstName() + " " + bill.getUser().getLastName());
            dto.setUserEmail(bill.getUser().getEmail());
            if (bill.getUser().getRoom() != null) {
                dto.setRoomNumber(bill.getUser().getRoom().getRoomNumber());
            }
        }

        if (bill.getIssuedBy() != null) {
            dto.setIssuedByName(bill.getIssuedBy().getFirstName() + " " + bill.getIssuedBy().getLastName());
        }

        if (bill.getPayment() != null) {
            dto.setTransactionId(bill.getPayment().getTransactionId());
            dto.setPaidDate(bill.getPayment().getPaymentDate());
        }

        return dto;
    }
}
