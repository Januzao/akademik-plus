package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.BillCreateDTO;
import com.akademikplus.akademik_plus.dto.BillResponseDTO;
import com.akademikplus.akademik_plus.entity.Bill;
import com.akademikplus.akademik_plus.entity.Payment;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.BillStatus;
import com.akademikplus.akademik_plus.enums.PaymentStatus;
import com.akademikplus.akademik_plus.exception.PaymentException;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.exception.ValidationException;
import com.akademikplus.akademik_plus.mapper.BillMapper;
import com.akademikplus.akademik_plus.repository.BillRepository;
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
public class BillService {

    private final BillRepository billRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final BillMapper billMapper;

    public List<BillResponseDTO> getAllBills() {
        return billRepository.findAll().stream()
                .map(billMapper::toResponse)
                .toList();
    }

    public List<BillResponseDTO> getBillsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return billRepository.findByUserIdOrderByIssuedDateDesc(userId).stream()
                .map(billMapper::toResponse)
                .toList();
    }

    public List<BillResponseDTO> getBillsForCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return billRepository.findByUserIdOrderByIssuedDateDesc(user.getId()).stream()
                .map(billMapper::toResponse)
                .toList();
    }

    public BillResponseDTO getById(Long id) {
        return billMapper.toResponse(
                billRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id))
        );
    }

    @Transactional
    public BillResponseDTO createBill(BillCreateDTO dto, String adminEmail) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminEmail));

        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be positive and greater than 0.");
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new ValidationException("Bill title is required.");
        }

        Bill bill = new Bill();
        bill.setUser(user);
        bill.setIssuedBy(admin);
        bill.setTitle(dto.getTitle());
        bill.setDescription(dto.getDescription());
        bill.setAmount(dto.getAmount());
        bill.setDueDate(dto.getDueDate());
        bill.setIssuedDate(LocalDate.now());
        bill.setStatus(BillStatus.PENDING);

        log.info("Bill created for userId={} by admin={}, amount={}", user.getId(), adminEmail, dto.getAmount());
        return billMapper.toResponse(billRepository.save(bill));
    }

    @Transactional
    public BillResponseDTO payBill(Long billId, String stripeToken, String userEmail) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        if (!bill.getUser().getId().equals(user.getId())) {
            throw new ValidationException("This bill does not belong to you.");
        }
        if (bill.getStatus() != BillStatus.PENDING) {
            throw new ValidationException("Only pending bills can be paid. Current status: " + bill.getStatus());
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(bill.getAmount());
        payment.setPaidFor(bill.getTitle());
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus(PaymentStatus.PENDING);

        try {
            int amountInCents = bill.getAmount().multiply(new BigDecimal(100)).intValue();

            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", amountInCents);
            chargeParams.put("currency", "pln");
            chargeParams.put("source", stripeToken);
            chargeParams.put("description", "Bill payment: " + bill.getTitle());

            Charge charge = Charge.create(chargeParams);

            if (charge.getPaid()) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setTransactionId(charge.getId());
                payment = paymentRepository.save(payment);

                bill.setStatus(BillStatus.PAID);
                bill.setPayment(payment);
                log.info("Bill {} paid by userId={}, chargeId={}", billId, user.getId(), charge.getId());
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                throw new PaymentException("Payment was not completed by Stripe.");
            }
        } catch (StripeException e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.error("Stripe error for billId={}, userId={}: {}", billId, user.getId(), e.getMessage());
            throw new PaymentException("Stripe error: " + e.getMessage());
        }

        return billMapper.toResponse(billRepository.save(bill));
    }

    @Transactional
    public BillResponseDTO cancelBill(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));

        if (bill.getStatus() == BillStatus.PAID) {
            throw new ValidationException("Cannot cancel a paid bill.");
        }

        bill.setStatus(BillStatus.CANCELLED);
        log.info("Bill {} cancelled", billId);
        return billMapper.toResponse(billRepository.save(bill));
    }
}
