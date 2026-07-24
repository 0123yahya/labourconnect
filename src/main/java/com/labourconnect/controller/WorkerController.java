package com.labourconnect.controller;

import com.labourconnect.entity.Worker;
import com.labourconnect.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;

    // Manual onboarding endpoint - this is how you'll register your first 100 workers,
    // e.g. by looping this call from a small script or a Postman collection.
    @PostMapping
    public ResponseEntity<Worker> createWorker(@RequestBody Worker worker) {
        Worker saved = workerService.createWorker(worker);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Worker> listWorkers() {
        return workerService.listWorkers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Worker> getWorker(@PathVariable Long id) {
        return workerService.getWorkerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}