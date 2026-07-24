package com.labourconnect.repository;

import com.labourconnect.enums.Skill;
import com.labourconnect.entity.Worker;
import com.labourconnect.enums.WorkerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    Optional<Worker> findByPhoneNumber(String phoneNumber);

    List<Worker> findBySkillAndAreaAndStatus(Skill skill, String area, WorkerStatus status);
}
