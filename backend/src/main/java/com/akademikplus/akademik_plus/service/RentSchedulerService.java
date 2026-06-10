package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.Role;
import com.akademikplus.akademik_plus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentSchedulerService {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void changeMonthRent() {
        log.info("Automatic monthly rent billing started.");

        List<User> users = userRepository.findAll();
        int charged = 0;

        for (User user : users) {
            if (user.getRoom() != null
                    && user.getRoom().getRentPrice() != null
                    && Boolean.TRUE.equals(user.getIsActive())
                    && user.getRole() == Role.STUDENT) {

                BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
                BigDecimal roomPrice = user.getRoom().getRentPrice();
                user.setBalance(currentBalance.subtract(roomPrice));
                charged++;
            }
        }

        userRepository.saveAll(users);
        log.info("Monthly rent billing completed. Charged {} students.", charged);
    }
}
