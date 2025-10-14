package com.Tipper.TipperHotelMongo.dto;


import com.Tipper.TipperHotelMongo.entity.Bookings;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.math.BigDecimal;
import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomDTO {
    private String roomId;
    private String roomType;
    private BigDecimal roomPrice;

    private String description;

    private List<Bookings> bookings = new ArrayList<>();
//    private String roomId;
    private List<BookingsDTO> reversedO;

    public void setBookings(List<BookingsDTO> reversed) {
        this.reversedO = reversed;
    }

}