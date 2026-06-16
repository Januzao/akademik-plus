package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.MaintenanceMessageDTO;
import com.akademikplus.akademik_plus.entity.MaintenanceMessage;
import com.akademikplus.akademik_plus.entity.MaintenanceRequest;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.Role;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.exception.ValidationException;
import com.akademikplus.akademik_plus.repository.MaintenanceMessageRepository;
import com.akademikplus.akademik_plus.repository.MaintenanceRequestRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceMessageService {

    private final MaintenanceMessageRepository messageRepository;
    private final MaintenanceRequestRepository requestRepository;
    private final UserRepository userRepository;

    public List<MaintenanceMessageDTO> getMessages(Long requestId, String callerEmail) {
        MaintenanceRequest request = findRequest(requestId);
        User caller = findUser(callerEmail);
        checkAccess(request, caller);

        return messageRepository.findByRequestIdOrderByCreatedAtAsc(requestId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public MaintenanceMessageDTO addMessage(Long requestId, String text, String callerEmail) {
        MaintenanceRequest request = findRequest(requestId);
        User caller = findUser(callerEmail);
        checkAccess(request, caller);

        MaintenanceMessage msg = new MaintenanceMessage();
        msg.setRequest(request);
        msg.setSender(caller);
        msg.setText(text);

        MaintenanceMessage saved = messageRepository.save(msg);
        log.info("Message added to requestId={} by userId={}", requestId, caller.getId());
        return toDTO(saved);
    }

    private MaintenanceRequest findRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found with id: " + id));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private void checkAccess(MaintenanceRequest request, User caller) {
        if (caller.getRole() == Role.ADMIN) return;
        if (!request.getUser().getId().equals(caller.getId())) {
            throw new ValidationException("Access denied: this request does not belong to you.");
        }
    }

    private MaintenanceMessageDTO toDTO(MaintenanceMessage msg) {
        MaintenanceMessageDTO dto = new MaintenanceMessageDTO();
        dto.setId(msg.getId());
        dto.setSenderName(msg.getSender().getFirstName() + " " + msg.getSender().getLastName());
        dto.setSenderRole(msg.getSender().getRole().name());
        dto.setText(msg.getText());
        dto.setCreatedAt(msg.getCreatedAt());
        return dto;
    }
}
