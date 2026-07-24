package com.labourconnect.controller;

import com.labourconnect.entity.Worker;
import com.labourconnect.repository.WorkerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    private final WorkerRepository workerRepository;

    public WorkerController(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    // Manual onboarding endpoint - this is how you'll register your first 100 workers,
    // e.g. by looping this call from a small script or a Postman collection.
    @PostMapping
    public ResponseEntity<Worker> createWorker(@RequestBody Worker worker) {
        Worker saved = workerRepository.save(worker);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Worker> listWorkers() {
        return workerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Worker> getWorker(@PathVariable Long id) {
        return workerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
