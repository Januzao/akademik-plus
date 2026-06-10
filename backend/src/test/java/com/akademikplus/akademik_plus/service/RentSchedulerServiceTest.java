package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.entity.Payment;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.PaymentStatus;
import com.akademikplus.akademik_plus.enums.Role;
import com.akademikplus.akademik_plus.repository.PaymentRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentSchedulerServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private RentSchedulerService rentSchedulerService;

    private User buildStudentWithRoom(Long id, BigDecimal balance, BigDecimal rentPrice) {
        Room room = new Room();
        room.setRoomNumber("101A");
        room.setRentPrice(rentPrice);

        User user = new User();
        user.setId(id);
        user.setEmail("student" + id + "@example.com");
        user.setRole(Role.STUDENT);
        user.setIsActive(true);
        user.setBalance(balance);
        user.setRoom(room);
        user.setFirstName("Student");
        user.setLastName("User");
        return user;
    }

    @Test
    void chargeMonthlyRent_completesPayment_whenBalanceSufficient() {
        User student = buildStudentWithRoom(1L, new BigDecimal("800.00"), new BigDecimal("650.00"));
        when(userRepository.findAll()).thenReturn(List.of(student));

        rentSchedulerService.chargeMonthlyRent();

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(student.getBalance()).isEqualByComparingTo("150.00");
    }

    @Test
    void chargeMonthlyRent_failsPayment_whenBalanceInsufficient() {
        User student = buildStudentWithRoom(2L, new BigDecimal("100.00"), new BigDecimal("650.00"));
        when(userRepository.findAll()).thenReturn(List.of(student));

        rentSchedulerService.chargeMonthlyRent();

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(student.getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void chargeMonthlyRent_skipsUsersWithoutRoom() {
        User userWithoutRoom = new User();
        userWithoutRoom.setId(3L);
        userWithoutRoom.setRole(Role.STUDENT);
        userWithoutRoom.setIsActive(true);
        userWithoutRoom.setBalance(BigDecimal.ZERO);

        when(userRepository.findAll()).thenReturn(List.of(userWithoutRoom));

        rentSchedulerService.chargeMonthlyRent();

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void chargeMonthlyRent_skipsAdminUsers() {
        User admin = new User();
        admin.setId(4L);
        admin.setRole(Role.ADMIN);
        admin.setIsActive(true);
        Room room = new Room();
        room.setRentPrice(new BigDecimal("650.00"));
        admin.setRoom(room);
        admin.setBalance(new BigDecimal("1000.00"));

        when(userRepository.findAll()).thenReturn(List.of(admin));

        rentSchedulerService.chargeMonthlyRent();

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void retryFailedBilling_completesRetry_whenBalanceSufficient() {
        User student = buildStudentWithRoom(1L, new BigDecimal("800.00"), new BigDecimal("650.00"));

        String currentPeriod = "Monthly rent — " + LocalDate.now().getMonth() + " " + LocalDate.now().getYear();
        Payment failedPayment = new Payment();
        failedPayment.setId(1L);
        failedPayment.setUser(student);
        failedPayment.setAmount(new BigDecimal("650.00"));
        failedPayment.setStatus(PaymentStatus.FAILED);
        failedPayment.setPaidFor(currentPeriod);

        when(paymentRepository.findByStatusAndPaidForContaining(PaymentStatus.FAILED, "Monthly rent"))
                .thenReturn(List.of(failedPayment));

        rentSchedulerService.retryFailedBilling();

        assertThat(failedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(student.getBalance()).isEqualByComparingTo("150.00");
        verify(userRepository).save(student);
        verify(paymentRepository).save(failedPayment);
    }

    @Test
    void retryFailedBilling_doesNotRetry_whenNoFailedPayments() {
        when(paymentRepository.findByStatusAndPaidForContaining(PaymentStatus.FAILED, "Monthly rent"))
                .thenReturn(List.of());

        rentSchedulerService.retryFailedBilling();

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void sendRentReminders_sendsEmailToAllStudentsWithRooms() {
        User student1 = buildStudentWithRoom(1L, new BigDecimal("800.00"), new BigDecimal("650.00"));
        User student2 = buildStudentWithRoom(2L, new BigDecimal("100.00"), new BigDecimal("650.00"));

        when(userRepository.findAll()).thenReturn(List.of(student1, student2));

        rentSchedulerService.sendRentReminders();

        verify(emailService, times(2)).sendRentReminderEmail(anyString(), anyString());
    }

    @Test
    void sendRentReminders_skipsStudentsWithoutRoom() {
        User studentWithoutRoom = new User();
        studentWithoutRoom.setId(3L);
        studentWithoutRoom.setRole(Role.STUDENT);
        studentWithoutRoom.setIsActive(true);

        when(userRepository.findAll()).thenReturn(List.of(studentWithoutRoom));

        rentSchedulerService.sendRentReminders();

        verify(emailService, never()).sendRentReminderEmail(any(), any());
    }
}
