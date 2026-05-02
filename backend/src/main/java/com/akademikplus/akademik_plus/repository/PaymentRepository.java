package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
