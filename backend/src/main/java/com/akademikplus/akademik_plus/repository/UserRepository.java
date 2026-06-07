package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    String email(String email);
}
