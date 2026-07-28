package com.labourconnect.service;

import com.labourconnect.enums.ConversationRole;
import com.labourconnect.enums.ResolutionStatus;

/**
 * Result of a role resolution attempt. role is populated only when
 * status == RESOLVED; it is null for NEEDS_ROLE_SELECTION and UNKNOWN_USER.
 */
public record RoleResolution(ResolutionStatus status, ConversationRole role) {
}