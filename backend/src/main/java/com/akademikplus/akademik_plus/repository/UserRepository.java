package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
