package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUserIdOrderByIssuedDateDesc(Long userId);
}
