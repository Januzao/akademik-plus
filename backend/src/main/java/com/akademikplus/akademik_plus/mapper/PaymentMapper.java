package com.akademikplus.akademik_plus.mapper;

import com.akademikplus.akademik_plus.dto.PaymentRequestDTO;
import com.akademikplus.akademik_plus.dto.PaymentResponseDTO;
import com.akademikplus.akademik_plus.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public PaymentResponseDTO toResponse(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaidFor(payment.getPaidFor());
        dto.setStatus(payment.getStatus());
        return dto;
    }

    public Payment toEntity(PaymentRequestDTO dto) {
        Payment payment = new Payment();
        payment.setPaidFor(dto.getPaidFor());
        payment.setAmount(dto.getAmount());
        payment.setTransactionId(dto.getTransactionId());
        return payment;
    }
}
