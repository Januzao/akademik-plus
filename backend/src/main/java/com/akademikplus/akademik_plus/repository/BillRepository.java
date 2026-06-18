package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUserIdOrderByIssuedDateDesc(Long userId);
    Optional<Bill> findTopByUserIdAndIssuedDateOrderByIdDesc(Long userId, LocalDate issuedDate);
    boolean existsByUserIdAndIssuedDateBetween(Long userId, LocalDate from, LocalDate to);
}
