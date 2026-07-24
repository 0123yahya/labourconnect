package com.labourconnect.controller;

import com.labourconnect.dto.JobRequest;
import com.labourconnect.entity.*;
import com.labourconnect.enums.JobStatus;
import com.labourconnect.enums.Skill;
import com.labourconnect.repository.JobOfferRepository;
import com.labourconnect.repository.JobRepository;
import com.labourconnect.service.ClientService;
import com.labourconnect.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobRepository jobRepository;
    private final ClientService clientService;
    private final JobOfferRepository jobOfferRepository;
    private final MatchingService matchingService;

    // Creates the job, finding the client by phone number or creating a new one -
    // this mirrors what the WhatsApp bot will do automatically in Stage 2.
    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody JobRequest request) {
        Client client = clientService.findOrCreateByPhoneNumber(
                request.getClientPhoneNumber(), request.getClientName());

        Job job = new Job();
        job.setClient(client);
        job.setServiceType(Skill.valueOf(request.getServiceType().toUpperCase()));
        job.setArea(request.getArea());
        job.setBudget(request.getBudget());
        if (request.getPreferredDate() != null && !request.getPreferredDate().isBlank()) {
            job.setPreferredDate(LocalDate.parse(request.getPreferredDate()));
        }
        job.setStatus(JobStatus.REQUESTED);

        return ResponseEntity.ok(jobRepository.save(job));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJob(@PathVariable Long id) {
        return jobRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Shows every worker who was offered this job and how they responded -
    // useful for seeing the broadcast-and-first-accept-wins logic in action.
    @GetMapping("/{id}/offers")
    public List<JobOffer> getOffers(@PathVariable Long id) {
        return jobOfferRepository.findByJobId(id);
    }

    // Triggers matching: finds eligible workers and creates a JobOffer for each.
    // In Stage 2, the bot calls this automatically right after the client finishes
    // the intake conversation.
    @PostMapping("/{id}/match")
    public ResponseEntity<?> matchJob(@PathVariable Long id) {
        List<JobOffer> offers = matchingService.matchJob(id);
        if (offers.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No matching workers found - job marked NO_MATCH"));
        }
        return ResponseEntity.ok(offers);
    }

    // Simulates a worker replying YES on WhatsApp - in Stage 2 this is called
    // from the webhook handler instead of Postman.
    @PostMapping("/{id}/accept/{workerId}")
    public ResponseEntity<?> acceptOffer(@PathVariable Long id, @PathVariable Long workerId) {
        try {
            Booking booking = matchingService.acceptOffer(id, workerId);
            return ResponseEntity.ok(booking);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Simulates a worker replying NO.
    @PostMapping("/{id}/decline/{workerId}")
    public ResponseEntity<?> declineOffer(@PathVariable Long id, @PathVariable Long workerId) {
        matchingService.declineOffer(id, workerId);
        return ResponseEntity.ok(Map.of("message", "Offer declined"));
    }
}