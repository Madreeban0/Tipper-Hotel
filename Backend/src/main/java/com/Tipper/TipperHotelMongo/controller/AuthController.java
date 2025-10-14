package com.Tipper.TipperHotelMongo.controller;

import com.Tipper.TipperHotelMongo.dto.LoginRequestDTO;
import com.Tipper.TipperHotelMongo.dto.ResponseDTO;
import com.Tipper.TipperHotelMongo.entity.User;
import com.Tipper.TipperHotelMongo.service.interfac.IUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IUserService userService;

    public AuthController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody User user){
        ResponseDTO responseDTO = userService.register(user);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
        ResponseDTO responseDTO = userService.Login(loginRequestDTO);
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }
}