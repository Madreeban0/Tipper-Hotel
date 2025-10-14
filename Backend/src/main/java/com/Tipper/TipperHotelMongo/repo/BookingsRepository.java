package com.Tipper.TipperHotelMongo.repo;

import com.Tipper.TipperHotelMongo.entity.Bookings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingsRepository extends MongoRepository<Bookings, String> {

    // ✅ Fixed property name capitalization
    Optional<Bookings> findByBookingConfirmationCode(String bookingConfirmationCode);

    // ✅ Fixed parameter name typo (Data → Date)
    @Query("{ 'checkInDate' : { $lte : ?1 }, 'checkOutDate' : { $gte : ?0 } }")
    List<Bookings> findBookingsByDateRange(LocalDate checkInDate, LocalDate checkOutDate);
}
