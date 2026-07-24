package com.labourconnect.service;

import com.labourconnect.entity.Booking;
import com.labourconnect.enums.BookingOutcome;
import com.labourconnect.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    // Records the final result of a booking (COMPLETED / NO_SHOW / CANCELLED) -
    // this is the data source the tiered no-show penalty system (warning -> pause
    // -> removal) will read from once it's built.
    public Optional<Booking> updateOutcome(Long bookingId, BookingOutcome outcome) {
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    booking.setOutcome(outcome);
                    return bookingRepository.save(booking);
                });
    }
}