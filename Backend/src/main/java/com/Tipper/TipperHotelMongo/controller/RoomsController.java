package com.Tipper.TipperHotelMongo.controller;

import com.Tipper.TipperHotelMongo.dto.ResponseDTO;
import com.Tipper.TipperHotelMongo.service.interfac.IRoomService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
public class RoomsController {
    private final IRoomService roomService;

    public RoomsController(IRoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> addNewRoom(
            @RequestParam(value = "roomType", required = false) String roomType,
            @RequestParam(value = "roomPrice", required = false)BigDecimal roomPrice,
            @RequestParam(value = "roomDescription", required = false) String roomDescription
            ) {
        if(roomType == null || roomType.isEmpty() || roomPrice == null){
            ResponseDTO response = new ResponseDTO();
            response.setStatusCode(404);
            response.setMessage("Please provide for all fields(roomType,roomPrice)");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

        ResponseDTO response = roomService.addNewRoom(roomType,roomPrice,roomDescription);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAllRooms() {
        ResponseDTO response = roomService.getAllRooms();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all-available-rooms")
    public ResponseEntity<ResponseDTO> getAllAvailableRooms(){
        ResponseDTO response = roomService.getAllAvailableRooms();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/types")
    public List<String> getRoomtypes(){
        return roomService.getAllRoomType();

    }

    @GetMapping("/room-by-roomid/{roomId}")
    public ResponseEntity<ResponseDTO> getRoomById(@PathVariable("roomId") String roomId){
        ResponseDTO response = roomService.getRoomById(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/available-rooms-by-date-and-time")
    public ResponseEntity<ResponseDTO> getAvailableRoomsByDateAndType(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate checkOutDate,
            @RequestParam(required = false) String roomType
            ){
        if(checkInDate == null || checkOutDate == null || roomType.isBlank()){
            ResponseDTO response = new ResponseDTO();
            response.setStatusCode(404);
            response.setMessage("All feilds are required(checkInDate, checkOutDate, roomType)");
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

        ResponseDTO response = roomService.getAvailableRoomsByDateAndType(checkInDate, checkOutDate, roomType);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> updateRooms(
            @PathVariable String roomId,
            @RequestParam(value = "roomType", required = false)String roomType,
            @RequestParam(value = "roomPrice", required = false)BigDecimal roomPrice,
            @RequestParam(value = "description", required = false)String description
            ){
        ResponseDTO response = roomService.updateRooms(roomId,roomType,description,roomPrice);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{roomId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteRooms(@PathVariable String roomId){
        ResponseDTO responseDTO = roomService.deleteRooms(roomId);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }
}