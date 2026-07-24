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
    private final WorkerService workerService;

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    // Records the final result of a booking (COMPLETED / NO_SHOW / CANCELLED).
    // When a booking transitions into NO_SHOW for the first time, applies the
    // tiered no-show penalty to the worker via WorkerService.
    public Optional<Booking> updateOutcome(Long bookingId, BookingOutcome outcome) {
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    boolean isNewNoShow = outcome == BookingOutcome.NO_SHOW
                            && booking.getOutcome() != BookingOutcome.NO_SHOW;

                    booking.setOutcome(outcome);
                    Booking saved = bookingRepository.save(booking);

                    if (isNewNoShow) {
                        workerService.recordNoShow(booking.getWorker().getId());
                    }

                    return saved;
                });
    }
}