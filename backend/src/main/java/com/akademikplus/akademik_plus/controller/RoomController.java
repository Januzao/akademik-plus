package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.RoomRequestDTO;
import com.akademikplus.akademik_plus.dto.RoomResponseDTO;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public List<RoomResponseDTO> getAll(){
        return roomService.findAll();
    }

    @GetMapping("/{id}")
    public RoomResponseDTO getById(@PathVariable Integer id) {
        return roomService.findById(id);
    }

    @PostMapping
    public ResponseEntity<RoomResponseDTO> create(@RequestBody RoomRequestDTO room) {
        return new ResponseEntity<>(roomService.create(room), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public RoomResponseDTO update(@PathVariable Integer id, @RequestBody RoomRequestDTO room) {
        return roomService.update(id, room);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
