package com.labourconnect.service;

import com.labourconnect.entity.*;
import com.labourconnect.enums.BookingOutcome;
import com.labourconnect.enums.JobStatus;
import com.labourconnect.enums.OfferResponse;
import com.labourconnect.enums.WorkerStatus;
import com.labourconnect.repository.BookingRepository;
import com.labourconnect.repository.JobOfferRepository;
import com.labourconnect.repository.JobRepository;
import com.labourconnect.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final JobRepository jobRepository;
    private final WorkerRepository workerRepository;
    private final JobOfferRepository jobOfferRepository;
    private final BookingRepository bookingRepository;

    /**
     * Finds all active workers matching the job's skill + area and broadcasts
     * a JobOffer to each of them. First one to accept wins (see acceptOffer).
     */
    public List<JobOffer> matchJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));

        List<Worker> matchingWorkers = workerRepository
                .findBySkillAndAreaAndStatus(job.getServiceType(), job.getArea(), WorkerStatus.ACTIVE);

        if (matchingWorkers.isEmpty()) {
            job.setStatus(JobStatus.NO_MATCH);
            jobRepository.save(job);
            return List.of();
        }

        job.setStatus(JobStatus.MATCHING);
        jobRepository.save(job);

        return matchingWorkers.stream()
                .map(worker -> {
                    JobOffer offer = new JobOffer();
                    offer.setJob(job);
                    offer.setWorker(worker);
                    offer.setResponse(OfferResponse.PENDING);
                    return jobOfferRepository.save(offer);
                })
                .toList();
    }

    /**
     * Called when a worker replies YES to a job offer.
     * Confirms the booking for that worker and expires every other pending
     * offer for the same job, since only one worker can take the job.
     */
    public Booking acceptOffer(Long jobId, Long workerId) {
        JobOffer offer = jobOfferRepository.findByJobIdAndWorkerId(jobId, workerId)
                .orElseThrow(() -> new IllegalArgumentException("No offer found for this job and worker"));

        if (offer.getResponse() != OfferResponse.PENDING) {
            throw new IllegalStateException("This offer is no longer available (already " + offer.getResponse() + ")");
        }

        offer.setResponse(OfferResponse.ACCEPTED);
        offer.setRespondedAt(LocalDateTime.now());
        jobOfferRepository.save(offer);

        // Expire every other pending offer for this job - only one worker gets it
        List<JobOffer> otherOffers = jobOfferRepository.findByJobIdAndResponse(jobId, OfferResponse.PENDING);
        for (JobOffer other : otherOffers) {
            other.setResponse(OfferResponse.EXPIRED);
            other.setRespondedAt(LocalDateTime.now());
            jobOfferRepository.save(other);
        }

        Job job = offer.getJob();
        job.setStatus(JobStatus.CONFIRMED);
        jobRepository.save(job);

        Booking booking = new Booking();
        booking.setJob(job);
        booking.setWorker(offer.getWorker());
        booking.setOutcome(BookingOutcome.PENDING);

        return bookingRepository.save(booking);
    }

    /**
     * Called when a worker replies NO to a job offer.
     */
    public void declineOffer(Long jobId, Long workerId) {
        JobOffer offer = jobOfferRepository.findByJobIdAndWorkerId(jobId, workerId)
                .orElseThrow(() -> new IllegalArgumentException("No offer found for this job and worker"));

        offer.setResponse(OfferResponse.DECLINED);
        offer.setRespondedAt(LocalDateTime.now());
        jobOfferRepository.save(offer);
    }
}