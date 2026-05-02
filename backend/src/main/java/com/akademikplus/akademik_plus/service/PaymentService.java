package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.entity.Payment;
import com.akademikplus.akademik_plus.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }
    
    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment did not found with id: " + id));
    }

    public Payment create(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }
}
