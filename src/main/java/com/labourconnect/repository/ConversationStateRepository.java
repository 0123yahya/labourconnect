package com.labourconnect.repository;

import com.labourconnect.entity.ConversationState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationStateRepository extends JpaRepository<ConversationState, Long> {

    Optional<ConversationState> findByPhoneNumber(String phoneNumber);
}