package com.akademikplus.akademik_plus.mapper;

import com.akademikplus.akademik_plus.dto.MaintenanceRequestReqDTO;
import com.akademikplus.akademik_plus.dto.MaintenanceRequestRespDTO;
import com.akademikplus.akademik_plus.entity.MaintenanceRequest;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceRequestMapper {
    public MaintenanceRequestRespDTO toResponse(MaintenanceRequest mr) {
        MaintenanceRequestRespDTO dto = new MaintenanceRequestRespDTO();
        dto.setId(mr.getId());
        dto.setCategory(mr.getCategory());
        dto.setPriority(mr.getPriority());
        dto.setDescription(mr.getDescription());
        dto.setRequestDate(mr.getRequestDate());
        dto.setPhotoUrl(mr.getPhotoUrl());

        if (mr.getRoom() != null) {
            dto.setRoomNumber(mr.getRoom().getRoomNumber());
        }

        if (mr.getUser() != null) {
            dto.setTenantName(mr.getUser().getFirstName() + " " + mr.getUser().getLastName());
            dto.setTenantPhone(mr.getUser().getPhone());
        }

        return dto;
    }


    public MaintenanceRequest toEntity(MaintenanceRequestReqDTO dto) {
        MaintenanceRequest entity = new MaintenanceRequest();

        entity.setCategory(dto.getCategory());
        entity.setPriority(dto.getPriority());
        entity.setDescription(dto.getDescription());
        entity.setPhotoUrl(dto.getPhotoUrl());

        return entity;
    }
}
