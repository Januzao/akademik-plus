package com.akademikplus.akademik_plus.controller;


import com.akademikplus.akademik_plus.dto.PaymentRequestDTO;
import com.akademikplus.akademik_plus.dto.PaymentResponseDTO;
import com.akademikplus.akademik_plus.entity.Payment;
import com.akademikplus.akademik_plus.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentResponseDTO> getAll(){
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    public PaymentResponseDTO getById(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> create(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        PaymentResponseDTO paymentResponseDTO = paymentService.createPayment(paymentRequestDTO);
        return new ResponseEntity<>(paymentResponseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
