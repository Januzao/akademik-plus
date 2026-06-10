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
import com.stripe.model.Refund;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            chargeParams.put("description", "Payment for: " + dto.getPaidFor());

            Charge charge = Charge.create(chargeParams);

            if (charge.getPaid()) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setTransactionId(charge.getId());

                BigDecimal current = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
                user.setBalance(current.add(amountToPay));
                userRepository.save(user);

                log.info("Payment completed for userId={}, amount={}, chargeId={}", user.getId(), amountToPay, charge.getId());
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                log.warn("Payment not completed by Stripe for userId={}, amount={}", user.getId(), amountToPay);
                throw new PaymentException("Payment was not completed by Stripe.");
            }
        } catch (StripeException e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.error("Stripe error for userId={}: {}", user.getId(), e.getMessage());
            throw new PaymentException("Stripe error: " + e.getMessage());
        }

        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponseDTO refund(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new ValidationException("Only completed payments can be refunded. Current status: " + payment.getStatus());
        }
        if (payment.getTransactionId() == null) {
            throw new ValidationException("Payment has no Stripe transaction ID — cannot refund.");
        }

        try {
            Map<String, Object> refundParams = new HashMap<>();
            refundParams.put("charge", payment.getTransactionId());
            Refund refund = Refund.create(refundParams);

            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setRefundId(refund.getId());
            payment.setRefundedAt(LocalDateTime.now());

            User user = payment.getUser();
            if (user != null) {
                BigDecimal current = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
                user.setBalance(current.subtract(payment.getAmount()));
                userRepository.save(user);
            }

            log.info("Payment refunded id={}, refundId={}", id, refund.getId());
            return paymentMapper.toResponse(paymentRepository.save(payment));

        } catch (StripeException e) {
            log.error("Stripe refund error for paymentId={}: {}", id, e.getMessage());
            throw new PaymentException("Stripe refund error: " + e.getMessage());
        }
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
