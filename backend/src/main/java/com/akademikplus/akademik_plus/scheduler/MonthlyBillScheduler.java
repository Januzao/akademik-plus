package com.akademikplus.akademik_plus.scheduler;

import com.akademikplus.akademik_plus.entity.Bill;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.BillStatus;
import com.akademikplus.akademik_plus.repository.BillRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyBillScheduler {

    private final UserRepository userRepository;
    private final BillRepository billRepository;

    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    //@Scheduled(cron = "0 0 0 1 * *")
    public void generateMonthlyRentBills() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
        LocalDate dueDate = today.withDayOfMonth(10);

        String monthLabel = today.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pl")));

        List<User> residents = userRepository.findAllByIsActiveTrueAndRoomIsNotNull();
        int created = 0;

        for (User user : residents) {
            if (billRepository.existsByUserIdAndIssuedDateBetween(user.getId(), monthStart, monthEnd)) {
                log.debug("Monthly bill already exists for userId={}, skipping", user.getId());
                continue;
            }

            Bill bill = new Bill();
            bill.setUser(user);
            bill.setIssuedBy(null);
            bill.setTitle("Czynsz za " + monthLabel);
            bill.setDescription("Pokój " + user.getRoom().getRoomNumber());
            bill.setAmount(user.getRoom().getRentPrice());
            bill.setIssuedDate(today);
            bill.setDueDate(dueDate);
            bill.setStatus(BillStatus.PENDING);

            billRepository.save(bill);
            created++;
            log.info("Monthly bill created for userId={}, room={}, amount={}",
                    user.getId(), user.getRoom().getRoomNumber(), user.getRoom().getRentPrice());
        }

        log.info("Monthly bill generation complete: {} bills created for {}", created, monthLabel);
    }
}
