package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.Payment;
import com.akademikplus.akademik_plus.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatusAndPaidForContaining(PaymentStatus status, String paidForFragment);
    List<Payment> findByUserId(Long userId);
    List<Payment> findByUserIdOrderByPaymentDateDesc(Long userId);
}
