package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.PaymentRequestDTO;
import com.akademikplus.akademik_plus.dto.PaymentResponseDTO;
import com.akademikplus.akademik_plus.entity.Payment;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.PaymentStatus;
import com.akademikplus.akademik_plus.exception.PaymentException;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.exception.ValidationException;
import com.akademikplus.akademik_plus.mapper.PaymentMapper;
import com.akademikplus.akademik_plus.repository.PaymentRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        BigDecimal amountToPay = dto.getAmount();
        if (amountToPay == null || amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be positive and greater than 0.");
        }

        Payment payment = paymentMapper.toEntity(dto);
        payment.setPaymentDate(LocalDate.now());
        payment.setUser(user);
        payment.setAmount(amountToPay);
        payment.setStatus(PaymentStatus.PENDING);

        try {
            int amountInCents = amountToPay.multiply(new BigDecimal(100)).intValue();

            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", amountInCents);
            chargeParams.put("currency", "pln");
            chargeParams.put("source", dto.getStripeToken());
            chargeParams.put("description", "Half/Full payment for: " + dto.getPaidFor());

            Charge charge = Charge.create(chargeParams);

            if (charge.getPaid()) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setTransactionId(charge.getId());

                BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
                user.setBalance(currentBalance.add(amountToPay));
                userRepository.save(user);

                log.info("Payment completed for user id={}, amount={}, transactionId={}", user.getId(), amountToPay, charge.getId());
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                log.warn("Payment not completed by Stripe for user id={}, amount={}", user.getId(), amountToPay);
                throw new PaymentException("Payment was not completed by Stripe.");
            }
        } catch (StripeException e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.error("Stripe error for user id={}: {}", user.getId(), e.getMessage());
            throw new PaymentException("Stripe error: " + e.getMessage());
        }

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(savedPayment);
    }

    public PaymentResponseDTO findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return paymentMapper.toResponse(payment);
    }

    public void delete(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
        log.info("Payment deleted id={}", id);
    }
}
