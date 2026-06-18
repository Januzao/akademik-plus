package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByRole(Role role);
    List<User> findAllByIsActiveTrueAndRoomIsNotNull();
}
