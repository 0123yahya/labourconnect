package com.labourconnect.service;

import com.labourconnect.dto.WorkerRequest;
import com.labourconnect.entity.Worker;
import com.labourconnect.enums.Skill;
import com.labourconnect.enums.WorkerStatus;
import com.labourconnect.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkerService {

    // Tiered no-show penalty thresholds: the 1st no-show is a silent warning
    // (count increments, worker stays ACTIVE), the 2nd triggers PAUSED, the 3rd+ triggers REMOVED.
    private static final int PAUSE_THRESHOLD = 2;
    private static final int REMOVAL_THRESHOLD = 3;

    private final WorkerRepository workerRepository;

    // Maps the incoming request DTO to a Worker entity and persists it -
    // this is how you'll register your first 100 workers, e.g. by looping this
    // call from a small script or a Postman collection.
    public Worker createWorker(WorkerRequest request) {
        Worker worker = new Worker();
        worker.setName(request.getName());
        worker.setPhoneNumber(request.getPhoneNumber());
        worker.setArea(request.getArea());
        worker.setSkill(parseSkill(request.getSkill()));
        return workerRepository.save(worker);
    }

    public List<Worker> listWorkers() {
        return workerRepository.findAll();
    }

    public Optional<Worker> getWorkerById(Long id) {
        return workerRepository.findById(id);
    }

    // Increments a worker's no-show count and applies the tiered penalty:
    // warning (1) -> PAUSED (2) -> REMOVED (3+). Called by BookingService whenever
    // a booking's outcome transitions to NO_SHOW.
    public Optional<Worker> recordNoShow(Long workerId) {
        return workerRepository.findById(workerId)
                .map(worker -> {
                    worker.setNoShowCount(worker.getNoShowCount() + 1);
                    if (worker.getNoShowCount() >= REMOVAL_THRESHOLD) {
                        worker.setStatus(WorkerStatus.REMOVED);
                    } else if (worker.getNoShowCount() >= PAUSE_THRESHOLD) {
                        worker.setStatus(WorkerStatus.PAUSED);
                    }
                    return workerRepository.save(worker);
                });
    }

    private Skill parseSkill(String skill) {
        try {
            return Skill.valueOf(skill.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid skill: " + skill);
        }
    }
}