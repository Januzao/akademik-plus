package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment did not found with id: " + id));
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User update(Integer id, User user) {
        User existing = findById(id);
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setEmail(user.getEmail());
        existing.setPhone(user.getPhone());
        existing.setRole(user.getRole());
        existing.setRoom(user.getRoom());
        existing.setIsActive(user.getIsActive());
        existing.setProfilePhoto(user.getProfilePhoto());
        return userRepository.save(existing);
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }
}
