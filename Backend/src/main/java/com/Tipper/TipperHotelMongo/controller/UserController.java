package com.Tipper.TipperHotelMongo.controller;


import com.Tipper.TipperHotelMongo.dto.ResponseDTO;
import com.Tipper.TipperHotelMongo.service.interfac.IUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllUsers(){
        ResponseDTO response = userService.getAllUsers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-by-id/{userId}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable("userId") String userId){
        ResponseDTO responseDTO = userService.getUserById(userId);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @GetMapping("/delete/{userId}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable("userId") String userId){
        ResponseDTO responseDTO = userService.deleteUser(userId);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @GetMapping("/get-user-bookings/{userId}")
    public ResponseEntity<ResponseDTO> getUserBookingsHistory(@PathVariable("userId") String userId){
        ResponseDTO responseDTO = userService.getUsersBookingsHistory(userId);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @GetMapping("/get-logged-in-profile-info")
    public ResponseEntity<ResponseDTO> getLoggedInProfileInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ResponseDTO responseDTO = userService.getMyInfo(email);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);

    }
}