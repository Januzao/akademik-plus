package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.Role;
import com.akademikplus.akademik_plus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentSchedulerService {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void changeMonthRent() {
        System.out.println("Launching automatic room charge billing.");

        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getRoom() != null
                    && user.getRoom().getRentPrice() != null
                    && Boolean.TRUE.equals(user.getIsActive())
                    && user.getRole() == Role.STUDENT) {

                BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
                BigDecimal roomPrice = user.getRoom().getRentPrice();

                user.setBalance(currentBalance.subtract(roomPrice));
            }
        }
        userRepository.saveAll(users);
        System.out.println("The charge has ended.");
    }
}
