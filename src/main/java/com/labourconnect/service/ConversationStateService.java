package com.labourconnect.service;

import com.labourconnect.entity.ConversationState;
import com.labourconnect.enums.ConversationRole;
import com.labourconnect.enums.ConversationStatus;
import com.labourconnect.repository.ConversationStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationStateService {

    private final ConversationStateRepository conversationStateRepository;

    public Optional<ConversationState> findByPhoneNumber(String phoneNumber) {
        return conversationStateRepository.findByPhoneNumber(phoneNumber);
    }

    // Finds the existing conversation state for a phone number, or starts a new
    // ACTIVE one with no step/context if this is the first message ever seen
    // from it. The future MessageRouterService calls this at the start of
    // handling every inbound message.
    public ConversationState findOrCreateByPhoneNumber(String phoneNumber) {
        return conversationStateRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> {
                    ConversationState state = new ConversationState();
                    state.setPhoneNumber(phoneNumber);
                    state.setStatus(ConversationStatus.ACTIVE);
                    return conversationStateRepository.save(state);
                });
    }

    // Starts (or restarts) a conversation session in a given role: sets the
    // active role, clears any in-progress step/context from a previous flow,
    // marks the session ACTIVE, and bumps the timestamp. This is a persistence
    // operation only - the caller (RoleResolutionService, and later
    // MessageRouterService) decides which role to activate; this method never
    // decides the role itself.
    public ConversationState activateConversationAs(Long id, ConversationRole role) {
        ConversationState state = conversationStateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ConversationState not found: " + id));

        state.setActiveRole(role);
        state.setCurrentStep(null);
        state.setContextData(null);
        state.setStatus(ConversationStatus.ACTIVE);
        state.setUpdatedAt(LocalDateTime.now());
        return conversationStateRepository.save(state);
    }

    // Advances a conversation to a new step, optionally updating the accumulated
    // context data collected so far. Called by the future MessageRouterService
    // once it has decided what step comes next - this method only persists that
    // decision, it doesn't make it.
    public ConversationState updateStep(Long id, String currentStep, String contextData) {
        ConversationState state = conversationStateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ConversationState not found: " + id));

        state.setCurrentStep(currentStep);
        if (contextData != null) {
            state.setContextData(contextData);
        }
        state.setUpdatedAt(LocalDateTime.now());
        return conversationStateRepository.save(state);
    }

    // Marks a conversation as COMPLETED, e.g. once a client finishes posting a
    // job or a worker responds to an offer.
    public ConversationState completeConversation(Long id) {
        ConversationState state = conversationStateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ConversationState not found: " + id));

        state.setStatus(ConversationStatus.COMPLETED);
        state.setUpdatedAt(LocalDateTime.now());
        return conversationStateRepository.save(state);
    }

    // Resets a conversation back to a fresh ACTIVE state with no step, role, or
    // context - for the fallback/timeout-reset handling planned in a later task.
    public ConversationState resetConversation(Long id) {
        ConversationState state = conversationStateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ConversationState not found: " + id));

        state.setCurrentStep(null);
        state.setContextData(null);
        state.setActiveRole(null);
        state.setStatus(ConversationStatus.ACTIVE);
        state.setUpdatedAt(LocalDateTime.now());
        return conversationStateRepository.save(state);
    }
}