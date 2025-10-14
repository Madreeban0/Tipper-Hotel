package com.Tipper.TipperHotelMongo.controller;

import com.Tipper.TipperHotelMongo.dto.ResponseDTO;
import com.Tipper.TipperHotelMongo.entity.Bookings;
import com.Tipper.TipperHotelMongo.service.interfac.IBookingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/bookings")
public class BookingsController {
    private final IBookingsService bookingsService;

    public BookingsController(IBookingsService bookingsService) {
        this.bookingsService = bookingsService;
    }

    @PostMapping("/book-room/{roomId}/{userId}/{bookingsRequest}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('user')")
    public ResponseEntity<ResponseDTO> saveBookings(
            @PathVariable String roomId,
            @PathVariable String userId,
            @PathVariable Bookings bookingsRequest

            
            ) {
        System.out.println("Save booking was hit");
        ResponseDTO response = bookingsService.saveBookings(roomId,userId,bookingsRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllBookings(){
        ResponseDTO response = bookingsService.getAllBookings();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("get-by-confirmatoin-code/{confirmationcode}")
    public ResponseEntity<ResponseDTO> getBookingByConfirmationcode(@PathVariable String confirmationcode){
        ResponseDTO response = bookingsService.findBookingByConfirmationCode(confirmationcode);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/cancel{bookingsId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO>  cancelBookings(@PathVariable String bookingsId){
        ResponseDTO response = bookingsService.cancelBookings(bookingsId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}