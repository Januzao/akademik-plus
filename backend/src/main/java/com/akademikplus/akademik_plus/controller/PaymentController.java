package com.akademikplus.akademik_plus.controller;


import com.akademikplus.akademik_plus.entity.Payment;
import com.akademikplus.akademik_plus.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<Payment> getAll(){
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    public Payment getById(@PathVariable Integer id) {
        return paymentService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Payment> create(@RequestBody Payment payment) {
        return new ResponseEntity<>(paymentService.create(payment), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
