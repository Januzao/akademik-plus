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
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private UserRepository userRepository;
    @Mock private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("test@example.com");
        user.setBalance(BigDecimal.ZERO);
        return user;
    }

    private Payment buildPayment(Long id, PaymentStatus status) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setAmount(new BigDecimal("650.00"));
        payment.setStatus(status);
        payment.setPaidFor("November rent");
        payment.setPaymentDate(LocalDate.now());
        return payment;
    }

    private PaymentResponseDTO buildResponse(Long id) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(id);
        return dto;
    }

    @Test
    void findAll_returnsMappedPayments() {
        Payment payment = buildPayment(1L, PaymentStatus.COMPLETED);
        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(buildResponse(1L));

        List<PaymentResponseDTO> result = paymentService.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findById_returnsPayment_whenExists() {
        Payment payment = buildPayment(1L, PaymentStatus.COMPLETED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(buildResponse(1L));

        PaymentResponseDTO result = paymentService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_throwsNotFound_whenMissing() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createPayment_throwsNotFound_whenUserMissing() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setUserId(99L);
        dto.setAmount(new BigDecimal("100.00"));

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createPayment_throwsValidation_whenAmountIsZero() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setUserId(1L);
        dto.setAmount(BigDecimal.ZERO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));

        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Amount must be positive");
    }

    @Test
    void createPayment_throwsValidation_whenAmountIsNegative() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setUserId(1L);
        dto.setAmount(new BigDecimal("-50.00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));

        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void createPayment_completesSuccessfully_whenStripeChargeSucceeds() throws Exception {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setUserId(1L);
        dto.setAmount(new BigDecimal("500.00"));
        dto.setPaidFor("November rent");
        dto.setStripeToken("tok_test");

        User user = buildUser(1L);
        Payment entity = buildPayment(null, PaymentStatus.PENDING);
        entity.setUser(user);
        Payment saved = buildPayment(1L, PaymentStatus.COMPLETED);
        saved.setUser(user);
        saved.setTransactionId("ch_test_123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentMapper.toEntity(dto)).thenReturn(entity);
        when(paymentRepository.save(any())).thenReturn(saved);
        when(userRepository.save(any())).thenReturn(user);
        when(paymentMapper.toResponse(saved)).thenReturn(buildResponse(1L));

        try (MockedStatic<Charge> chargeStatic = mockStatic(Charge.class)) {
            Charge mockCharge = mock(Charge.class);
            when(mockCharge.getPaid()).thenReturn(true);
            when(mockCharge.getId()).thenReturn("ch_test_123");
            chargeStatic.when(() -> Charge.create(any(Map.class))).thenReturn(mockCharge);

            PaymentResponseDTO result = paymentService.createPayment(dto);
            assertThat(result.getId()).isEqualTo(1L);
        }
    }

    @Test
    void createPayment_throwsPaymentException_whenStripeChargeNotPaid() throws Exception {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setUserId(1L);
        dto.setAmount(new BigDecimal("500.00"));
        dto.setStripeToken("tok_test");

        User user = buildUser(1L);
        Payment entity = buildPayment(null, PaymentStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentMapper.toEntity(dto)).thenReturn(entity);

        try (MockedStatic<Charge> chargeStatic = mockStatic(Charge.class)) {
            Charge mockCharge = mock(Charge.class);
            when(mockCharge.getPaid()).thenReturn(false);
            chargeStatic.when(() -> Charge.create(any(Map.class))).thenReturn(mockCharge);

            assertThatThrownBy(() -> paymentService.createPayment(dto))
                    .isInstanceOf(PaymentException.class)
                    .hasMessageContaining("not completed by Stripe");
        }
    }

    @Test
    void refund_throwsNotFound_whenPaymentMissing() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.refund(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void refund_throwsValidation_whenStatusNotCompleted() {
        Payment payment = buildPayment(1L, PaymentStatus.PENDING);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.refund(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Only completed payments");
    }

    @Test
    void refund_throwsValidation_whenNoTransactionId() {
        Payment payment = buildPayment(1L, PaymentStatus.COMPLETED);
        payment.setTransactionId(null);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.refund(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("no Stripe transaction ID");
    }

    @Test
    void refund_completesSuccessfully_whenStripeRefundSucceeds() throws Exception {
        User user = buildUser(1L);
        user.setBalance(new BigDecimal("650.00"));

        Payment payment = buildPayment(1L, PaymentStatus.COMPLETED);
        payment.setTransactionId("ch_test_123");
        payment.setUser(user);

        Payment savedPayment = buildPayment(1L, PaymentStatus.REFUNDED);
        savedPayment.setRefundId("re_test_456");
        savedPayment.setUser(user);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(savedPayment);
        when(userRepository.save(user)).thenReturn(user);
        when(paymentMapper.toResponse(savedPayment)).thenReturn(buildResponse(1L));

        try (MockedStatic<Refund> refundStatic = mockStatic(Refund.class)) {
            Refund mockRefund = mock(Refund.class);
            when(mockRefund.getId()).thenReturn("re_test_456");
            refundStatic.when(() -> Refund.create(any(Map.class))).thenReturn(mockRefund);

            PaymentResponseDTO result = paymentService.refund(1L);
            assertThat(result.getId()).isEqualTo(1L);
        }

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(payment.getRefundId()).isEqualTo("re_test_456");
    }

    @Test
    void delete_deletesPayment() {
        when(paymentRepository.existsById(1L)).thenReturn(true);

        paymentService.delete(1L);

        verify(paymentRepository).deleteById(1L);
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(paymentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> paymentService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
