package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.PaymentRequestDTO;
import com.akademikplus.akademik_plus.dto.PaymentResponseDTO;
import com.akademikplus.akademik_plus.entity.Payment;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.mapper.PaymentMapper;
import com.akademikplus.akademik_plus.repository.PaymentRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    
    public List<PaymentResponseDTO> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO dto) {
        Payment payment = paymentMapper.toEntity(dto);

        payment.setPaymentDate(LocalDate.now());
        User user = userRepository.findById(dto.getUserId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        payment.setUser(user);

        BigDecimal amountToPay = dto.getAmount();

        if (amountToPay == null || amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive and greater than 0.");
        }

        payment.setAmount(amountToPay);

        try {
            int amountInCents = amountToPay.multiply(new BigDecimal(100)).intValue();

            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", amountInCents);
            chargeParams.put("currency", "pln");
            chargeParams.put("source", dto.getStripeToken());
            chargeParams.put("description", "Half/Full payment for: " + dto.getPaidFor());

            Charge charge = Charge.create(chargeParams);

            if (charge.getPaid()) {
                payment.setStatus("Payment completed");
                payment.setTransactionId(charge.getId());


                BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
                user.setBalance(currentBalance.add(amountToPay));
                userRepository.save(user);
            } else {
                payment.setStatus("Failed");
            }
        } catch (StripeException e) {
            payment.setStatus("Failed");
        }

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(savedPayment);
    }
    
    public PaymentResponseDTO findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found by id: " + id));

        return paymentMapper.toResponse(payment);
    }

    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }
}
