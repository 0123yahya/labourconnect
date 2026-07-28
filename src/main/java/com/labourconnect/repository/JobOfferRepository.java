package com.labourconnect.repository;

import com.labourconnect.entity.JobOffer;
import com.labourconnect.enums.OfferResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

    List<JobOffer> findByJobId(Long jobId);

    Optional<JobOffer> findByJobIdAndWorkerId(Long jobId, Long workerId);

    List<JobOffer> findByJobIdAndResponse(Long jobId, OfferResponse response);

    boolean existsByWorker_PhoneNumberAndResponse(String phoneNumber, OfferResponse response);
}