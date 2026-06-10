package com.akademikplus.akademik_plus.mapper;

import com.akademikplus.akademik_plus.dto.PaymentRequestDTO;
import com.akademikplus.akademik_plus.dto.PaymentResponseDTO;
import com.akademikplus.akademik_plus.entity.Payment;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMapperTest {

    private final PaymentMapper mapper = new PaymentMapper();

    @Test
    void toResponse_mapsAllFields_withUser() {
        Room room = new Room();
        room.setRoomNumber("101A");

        User user = new User();
        user.setId(5L);
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setRoom(room);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("650.00"));
        payment.setPaidFor("November rent");
        payment.setPaymentDate(LocalDate.of(2024, 11, 1));
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId("ch_test_123");
        payment.setRefundId("re_test_456");
        payment.setRefundedAt(LocalDateTime.of(2024, 11, 5, 10, 0));
        payment.setUser(user);

        PaymentResponseDTO dto = mapper.toResponse(payment);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getAmount()).isEqualByComparingTo("650.00");
        assertThat(dto.getPaidFor()).isEqualTo("November rent");
        assertThat(dto.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(dto.getTransactionId()).isEqualTo("ch_test_123");
        assertThat(dto.getRefundId()).isEqualTo("re_test_456");
        assertThat(dto.getTenantId()).isEqualTo(5L);
        assertThat(dto.getTenantName()).isEqualTo("Jan Kowalski");
        assertThat(dto.getRoomNumber()).isEqualTo("101A");
    }

    @Test
    void toResponse_noTenantInfo_whenUserNull() {
        Payment payment = new Payment();
        payment.setId(2L);
        payment.setAmount(new BigDecimal("300.00"));
        payment.setStatus(PaymentStatus.PENDING);

        PaymentResponseDTO dto = mapper.toResponse(payment);

        assertThat(dto.getTenantId()).isNull();
        assertThat(dto.getTenantName()).isNull();
        assertThat(dto.getRoomNumber()).isNull();
    }

    @Test
    void toResponse_noRoomNumber_whenUserHasNoRoom() {
        User user = new User();
        user.setId(3L);
        user.setFirstName("Anna");
        user.setLastName("Nowak");

        Payment payment = new Payment();
        payment.setId(3L);
        payment.setAmount(new BigDecimal("200.00"));
        payment.setUser(user);

        PaymentResponseDTO dto = mapper.toResponse(payment);

        assertThat(dto.getTenantName()).isEqualTo("Anna Nowak");
        assertThat(dto.getRoomNumber()).isNull();
    }

    @Test
    void toEntity_mapsPaidForAndAmount() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setPaidFor("December rent");
        dto.setAmount(new BigDecimal("700.00"));
        dto.setStripeToken("tok_test");

        Payment payment = mapper.toEntity(dto);

        assertThat(payment.getPaidFor()).isEqualTo("December rent");
        assertThat(payment.getAmount()).isEqualByComparingTo("700.00");
        assertThat(payment.getTransactionId()).isNull();
    }
}
