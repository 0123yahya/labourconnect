package com.labourconnect.service;

import com.labourconnect.entity.ConversationState;
import com.labourconnect.enums.ConversationRole;
import com.labourconnect.enums.ConversationStatus;
import com.labourconnect.enums.OfferResponse;
import com.labourconnect.enums.ResolutionStatus;
import com.labourconnect.repository.ClientRepository;
import com.labourconnect.repository.JobOfferRepository;
import com.labourconnect.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Decides which marketplace role (CLIENT / WORKER) a conversation should be
 * treated as, using the agreed priority order:
 *   1. Pending offer / worker-side context - unambiguous, wins immediately.
 *   2. Existing active conversation role - already decided for this session.
 *   3. Registered as both CLIENT and WORKER - ambiguous, needs an explicit ask.
 *   4. Registered as exactly one - use it.
 *   5. Registered as neither - unknown user.
 *
 * This service only decides a role - it never persists a ConversationState
 * (that's ConversationStateService's job) and knows nothing about WhatsApp
 * messaging or controllers.
 *
 * IMPORTANT: ConversationRole represents marketplace conversation intent only
 * (who is this message for - hiring, or looking for work). It must never be
 * used to represent platform privilege levels (e.g. Admin). A privilege check,
 * if ever needed, belongs in a future caller (e.g. MessageRouterService)
 * BEFORE this service is invoked, not inside it.
 */
@Service
@RequiredArgsConstructor
public class RoleResolutionService {

    private final WorkerRepository workerRepository;
    private final ClientRepository clientRepository;
    private final JobOfferRepository jobOfferRepository;

    public RoleResolution resolveRole(String phoneNumber, ConversationState conversationState) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return new RoleResolution(ResolutionStatus.UNKNOWN_USER, null);
        }

        // Step 1: an unresponded job offer is unambiguous worker-side context -
        // a YES/NO reply is clearly about that offer, regardless of any other
        // registration this phone number might have.
        if (jobOfferRepository.existsByWorker_PhoneNumberAndResponse(phoneNumber, OfferResponse.PENDING)) {
            return new RoleResolution(ResolutionStatus.RESOLVED, ConversationRole.WORKER);
        }

        // Step 2: stay on whatever role this session already committed to,
        // rather than re-resolving mid-flow.
        if (conversationState != null
                && conversationState.getStatus() == ConversationStatus.ACTIVE
                && conversationState.getActiveRole() != null) {
            return new RoleResolution(ResolutionStatus.RESOLVED, conversationState.getActiveRole());
        }

        // Steps 3-5: resolve from registration. Built as a growing list rather
        // than nested conditionals so future marketplace roles (Supplier,
        // Builder, etc.) can be added as one more repository check appended
        // here, without restructuring the branching logic below.
        List<ConversationRole> registeredRoles = new ArrayList<>();
        if (workerRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            registeredRoles.add(ConversationRole.WORKER);
        }
        if (clientRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            registeredRoles.add(ConversationRole.CLIENT);
        }

        if (registeredRoles.size() > 1) {
            return new RoleResolution(ResolutionStatus.NEEDS_ROLE_SELECTION, null);
        }
        if (registeredRoles.size() == 1) {
            return new RoleResolution(ResolutionStatus.RESOLVED, registeredRoles.get(0));
        }

        return new RoleResolution(ResolutionStatus.UNKNOWN_USER, null);
    }
}