package com.Tipper.TipperHotelMongo.service.impl;

import com.Tipper.TipperHotelMongo.dto.BookingsDTO;
import com.Tipper.TipperHotelMongo.dto.ResponseDTO;
import com.Tipper.TipperHotelMongo.entity.Bookings;
import com.Tipper.TipperHotelMongo.entity.Rooms;
import com.Tipper.TipperHotelMongo.entity.User;
import com.Tipper.TipperHotelMongo.exception.OurException;
import com.Tipper.TipperHotelMongo.repo.BookingsRepository;
import com.Tipper.TipperHotelMongo.repo.RoomsRepository;
import com.Tipper.TipperHotelMongo.repo.UserRepository;
import com.Tipper.TipperHotelMongo.service.interfac.IBookingsService;
import com.Tipper.TipperHotelMongo.utils.Utils;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingsService implements IBookingsService {

    private final BookingsRepository bookingsRepository;
    private final RoomsRepository roomsRepository;
    private final UserRepository userRepository;

    public BookingsService(BookingsRepository bookingsRepository, RoomsRepository roomsRepository, UserRepository userRepository) {
        this.bookingsRepository = bookingsRepository;
        this.roomsRepository = roomsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseDTO saveBookings(String roomId, String userId, Bookings bookingsRequest) {
        ResponseDTO response = new ResponseDTO();
        try {
            // Parse dates
            LocalDate checkIn = LocalDate.parse(bookingsRequest.getCheckInDate());
            LocalDate checkOut = LocalDate.parse(bookingsRequest.getCheckOutDate());

            // Validate check-in and check-out order
            if (checkOut.isBefore(checkIn)) {
                throw new IllegalArgumentException("Check-out date cannot be before check-in date");
            }

            // Get Room and User
            Rooms rooms = roomsRepository.findById(roomId)
                    .orElseThrow(() -> new OurException("Room not found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new OurException("User not found"));

            // Check room availability
            List<Bookings> existingBooking = rooms.getBookings();
            if (!roomIsAvailable(bookingsRequest, existingBooking)) {
                throw new OurException("For the selected date range there are no rooms available");
            }

            // Generate booking confirmation code
            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);

            bookingsRequest.setRoom(rooms);
            bookingsRequest.setUser(user);
            bookingsRequest.setBookingConfirmationCode(bookingConfirmationCode);

            // Save booking
            Bookings savedBookings = bookingsRepository.save(bookingsRequest);

            // Update User bookings
            List<Bookings> userBookings = user.getBookings();
            userBookings.add(savedBookings);
            user.setBookings(userBookings);
            userRepository.save(user);

            // Update Room bookings
            List<Bookings> roomBookings = rooms.getBookings();
            roomBookings.add(savedBookings);
            rooms.setBookings(roomBookings);
            roomsRepository.save(rooms);

            // Build success response
            response.setStatusCode(200);
            response.setMessage("Your booking was successful");
            response.setBookingsConfirmationCode(bookingConfirmationCode);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while saving your booking: " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO findBookingByConfirmationCode(String confirmationcode) {
        ResponseDTO response = new ResponseDTO();
        try {
            Bookings bookings = bookingsRepository.findByBookingConfirmationCode(confirmationcode)
                    .orElseThrow(() -> new OurException("Booking not found"));
            BookingsDTO bookingsDTO = Utils.mapBookingEntityToBookingDTO(bookings);
            response.setStatusCode(200);
            response.setMessage("Room booking successful!");
            response.setBookings(bookingsDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while fetching booking");
        }
        return response;
    }

    @Override
    public ResponseDTO getAllBookings() {
        ResponseDTO response = new ResponseDTO();
        try {
            List<Bookings> bookings = bookingsRepository.findAll(Sort.by(Sort.Direction.DESC, "bookingId"));
            List<BookingsDTO> bookingsDTOList = Utils.mapBookingListEntityToBookingListDTO(bookings);

            response.setStatusCode(200);
            response.setMessage("All bookings fetched successfully");
            response.setBookingsList(bookingsDTOList);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error while fetching all bookings");
        }
        return response;
    }

    @Override
    public ResponseDTO cancelBookings(String bookingId) {
        ResponseDTO response = new ResponseDTO();
        try {
            Bookings bookings = bookingsRepository.findById(bookingId)
                    .orElseThrow(() -> new OurException("Booking not found"));
            User user = bookings.getUser();

            if (user != null) {
                user.getBookings().removeIf(b -> b.getId().equals(bookingId));
                userRepository.save(user);
            }

            Rooms rooms = bookings.getRoom();
            if (rooms != null) {
                rooms.getBookings().removeIf(b -> b.getId().equals(bookingId));
                roomsRepository.save(rooms);
            }

            bookingsRepository.deleteById(bookingId);

            response.setStatusCode(200);
            response.setMessage("Booking cancelled successfully");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while cancelling your booking: " + e.getMessage());
        }
        return response;
    }

    private boolean roomIsAvailable(Bookings bookingsRequest, List<Bookings> existingBookings) {
        LocalDate newCheckIn = LocalDate.parse(bookingsRequest.getCheckInDate());
        LocalDate newCheckOut = LocalDate.parse(bookingsRequest.getCheckOutDate());

        return existingBookings.stream().noneMatch(existingBooking -> {
            LocalDate existCheckIn = LocalDate.parse(existingBooking.getCheckInDate());
            LocalDate existCheckOut = LocalDate.parse(existingBooking.getCheckOutDate());

            // Overlap check
            return (newCheckIn.isBefore(existCheckOut) && newCheckOut.isAfter(existCheckIn))
                    || newCheckIn.equals(existCheckIn)
                    || newCheckOut.equals(existCheckOut);
        });
    }
}
