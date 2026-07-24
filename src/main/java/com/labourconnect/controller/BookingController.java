package com.labourconnect.controller;

import com.labourconnect.entity.Booking;
import com.labourconnect.enums.BookingOutcome;
import com.labourconnect.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Records the final result of a booking, e.g. POST /api/bookings/5/outcome/NO_SHOW -
    // this is what the no-show penalty system will read from once it's built.
    @PostMapping("/{id}/outcome/{outcome}")
    public ResponseEntity<?> updateOutcome(@PathVariable Long id, @PathVariable String outcome) {
        BookingOutcome parsedOutcome;
        try {
            parsedOutcome = BookingOutcome.valueOf(outcome.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid outcome: " + outcome));
        }

        return bookingService.updateOutcome(id, parsedOutcome)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}