package com.labourconnect.service;

import com.labourconnect.entity.Worker;
import com.labourconnect.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;

    public Worker createWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    public List<Worker> listWorkers() {
        return workerRepository.findAll();
    }

    public Optional<Worker> getWorkerById(Long id) {
        return workerRepository.findById(id);
    }
}