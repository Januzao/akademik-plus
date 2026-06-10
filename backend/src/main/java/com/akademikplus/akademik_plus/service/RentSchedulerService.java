package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.entity.Payment;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.PaymentStatus;
import com.akademikplus.akademik_plus.enums.Role;
import com.akademikplus.akademik_plus.repository.PaymentRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentSchedulerService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    // Runs on the 1st of every month at midnight
    @Scheduled(cron = "0 0 0 1 * ?")
    public void chargeMonthlyRent() {
        log.info("Monthly rent billing started.");

        List<User> students = userRepository.findAll().stream()
                .filter(u -> u.getRoom() != null
                        && u.getRoom().getRentPrice() != null
                        && Boolean.TRUE.equals(u.getIsActive())
                        && u.getRole() == Role.STUDENT)
                .toList();

        int charged = 0;
        int failed = 0;

        for (User user : students) {
            BigDecimal rentPrice = user.getRoom().getRentPrice();
            BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

            Payment payment = new Payment();
            payment.setUser(user);
            payment.setAmount(rentPrice);
            payment.setPaidFor("Monthly rent — " + LocalDate.now().getMonth() + " " + LocalDate.now().getYear());
            payment.setPaymentDate(LocalDate.now());

            if (currentBalance.compareTo(rentPrice) >= 0) {
                user.setBalance(currentBalance.subtract(rentPrice));
                payment.setStatus(PaymentStatus.COMPLETED);
                charged++;
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                failed++;
                log.warn("Insufficient balance for userId={}, email={}, balance={}, rent={}",
                        user.getId(), user.getEmail(), currentBalance, rentPrice);
            }
            paymentRepository.save(payment);
        }

        userRepository.saveAll(students);
        log.info("Monthly rent billing completed. Charged: {}, Failed: {}", charged, failed);
    }

    // Runs daily at 9am to retry failed billing from the current month
    @Scheduled(cron = "0 0 9 2-28 * ?")
    public void retryFailedBilling() {
        LocalDate today = LocalDate.now();
        String currentPeriod = "Monthly rent — " + today.getMonth() + " " + today.getYear();

        List<Payment> failedPayments = paymentRepository
                .findByStatusAndPaidForContaining(PaymentStatus.FAILED, "Monthly rent");

        if (failedPayments.isEmpty()) return;

        log.info("Retrying {} failed billing payment(s).", failedPayments.size());

        for (Payment payment : failedPayments) {
            if (!payment.getPaidFor().contains(currentPeriod)) continue;

            User user = payment.getUser();
            BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
            BigDecimal rent = payment.getAmount();

            if (balance.compareTo(rent) >= 0) {
                user.setBalance(balance.subtract(rent));
                payment.setStatus(PaymentStatus.COMPLETED);
                userRepository.save(user);
                paymentRepository.save(payment);
                log.info("Retry billing succeeded for userId={}, email={}", user.getId(), user.getEmail());
            }
        }
    }

    // Runs on the 25th of every month at 9am — sends reminders
    @Scheduled(cron = "0 0 9 25 * ?")
    public void sendRentReminders() {
        log.info("Sending monthly rent reminders.");

        List<User> students = userRepository.findAll().stream()
                .filter(u -> u.getRoom() != null
                        && u.getRoom().getRentPrice() != null
                        && Boolean.TRUE.equals(u.getIsActive())
                        && u.getRole() == Role.STUDENT)
                .toList();

        int reminded = 0;
        for (User user : students) {
            BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
            BigDecimal rent = user.getRoom().getRentPrice();

            String message = "Reminder: your monthly rent of " + rent + " PLN will be charged on the 1st of next month.\n"
                    + "Your current balance: " + balance + " PLN.\n"
                    + (balance.compareTo(rent) < 0
                    ? "WARNING: Your balance is insufficient. Please top up before the 1st."
                    : "Your balance is sufficient.");

            emailService.sendRentReminderEmail(user.getEmail(), message);
            reminded++;
        }

        log.info("Rent reminders sent to {} students.", reminded);
    }
}
